import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class AstDump {
    private static final Pattern LINE_CONTINUATION =
        Pattern.compile("\\\\[ \\t]*(?:\\r?\\n|\\r)");
    private static final Pattern CONTINUATION_COMMENT =
        Pattern.compile("(?m)^[ \\t]*#[^\\r\\n]*(?:\\r?\\n|\\r|\\n)");
    // DockerfileLexer.g4 treats parser directives as comments and only supports '\'.
    private static final char GRAMMAR_ESCAPE_TOKEN = '\\';

    private static String source;
    private static int[] codePointOffsets;
    private static CommonTokenStream tokens;

    private AstDump() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: AstDump DOCKERFILE");
            System.exit(2);
        }

        Path input = Path.of(args[0]);
        try {
            source = Files.readString(input, StandardCharsets.UTF_8);
            codePointOffsets = codePointOffsets(source);
        } catch (IOException error) {
            System.err.printf("%s: %s%n", input, error.getMessage());
            System.exit(2);
            return;
        }

        DiagnosticListener diagnostics = new DiagnosticListener(input.toString());
        DockerfileLexer lexer = new DockerfileLexer(CharStreams.fromString(source, input.toString()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(diagnostics);

        tokens = new CommonTokenStream(lexer);
        DockerfileParser parser = new DockerfileParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(diagnostics);

        DockerfileParser.DockerfileContext tree = parser.dockerfile();
        tokens.fill();

        if (!diagnostics.messages.isEmpty()) {
            diagnostics.messages.forEach(System.err::println);
            System.exit(1);
        }

        Document document = new Document();
        for (DockerfileParser.ElementContext element : tree.element()) {
            if (element.instruction() != null) {
                document.instructions.add(toInstruction(element.instruction()));
            }
        }

        byte[] output = Json.write(document).getBytes(StandardCharsets.UTF_8);
        System.out.write(output, 0, output.length);
    }

    private static Instruction toInstruction(DockerfileParser.InstructionContext wrapper) {
        ParseTree firstChild = wrapper.getChild(0);
        if (!(firstChild instanceof org.antlr.v4.runtime.ParserRuleContext)) {
            throw new IllegalStateException("instruction has no parser-rule child");
        }

        org.antlr.v4.runtime.ParserRuleContext context =
            (org.antlr.v4.runtime.ParserRuleContext) firstChild;
        Token commandToken = context.getStart();

        Instruction instruction = new Instruction();
        instruction.command = commandToken.getText().toLowerCase(Locale.ROOT);
        instruction.location = new Location(
            commandToken.getLine(),
            context.getStop().getLine()
        );

        Token argumentStart = commandToken;
        DockerfileParser.Builder_flagsContext builderFlags = directBuilderFlags(context);
        if (builderFlags != null) {
            for (TerminalNode flag : builderFlags.BUILDER_FLAG()) {
                String value = decodeBuilderFlag(flag.getText());
                if (!"--".equals(value)) {
                    instruction.flags.add(value);
                }
            }
            argumentStart = builderFlags.getStop();
        }

        DockerfileParser.Json_arrayContext json = directJsonArray(context);
        if (json != null) {
            instruction.argumentKind = "json";
            List<String> values = new ArrayList<>();
            for (TerminalNode string : json.STRING()) {
                values.add(decodeString(string.getText()));
            }
            instruction.arguments = values;
        } else {
            instruction.argumentKind = "text";
            instruction.arguments = argumentText(argumentStart, context.getStop());
        }

        if ("onbuild".equals(instruction.command)) {
            DockerfileParser.Onbuild_instContext onbuild =
                (DockerfileParser.Onbuild_instContext) context;
            if (onbuild.instruction() != null) {
                instruction.children.add(toInstruction(onbuild.instruction()));
            }
        }

        return instruction;
    }

    private static DockerfileParser.Builder_flagsContext directBuilderFlags(
        org.antlr.v4.runtime.ParserRuleContext context
    ) {
        if (context.children == null) {
            return null;
        }
        for (ParseTree child : context.children) {
            if (child instanceof DockerfileParser.Builder_flagsContext) {
                return (DockerfileParser.Builder_flagsContext) child;
            }
        }
        return null;
    }

    private static DockerfileParser.Json_arrayContext directJsonArray(
        org.antlr.v4.runtime.ParserRuleContext context
    ) {
        if (context.children == null) {
            return null;
        }
        for (ParseTree child : context.children) {
            if (child instanceof DockerfileParser.Json_arrayContext) {
                return (DockerfileParser.Json_arrayContext) child;
            }
        }
        return null;
    }

    private static String argumentText(Token command, Token stop) {
        List<Token> visible = tokens.getTokens(
            command.getTokenIndex() + 1,
            stop.getTokenIndex() - 1
        );
        if (visible == null || visible.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        Token previous = null;
        for (Token token : visible) {
            if (token.getType() == Token.EOF || token.getType() == DockerfileLexer.NL) {
                continue;
            }
            if (previous != null && hasLogicalGap(previous, token)) {
                result.append(' ');
            }
            result.append(logicalTokenText(token.getText()));
            previous = token;
        }
        return normalizeText(result.toString());
    }

    private static boolean hasLogicalGap(Token previous, Token current) {
        int start = previous.getStopIndex() + 1;
        int end = current.getStartIndex();
        if (start < 0 || end <= start || start >= codePointOffsets.length) {
            return false;
        }

        int charStart = codePointOffsets[start];
        int charEnd = codePointOffsets[Math.min(end, codePointOffsets.length - 1)];
        String gap = source.substring(charStart, charEnd);
        gap = LINE_CONTINUATION.matcher(gap).replaceAll("");
        gap = CONTINUATION_COMMENT.matcher(gap).replaceAll("");
        return !gap.isEmpty();
    }

    private static String logicalTokenText(String value) {
        value = LINE_CONTINUATION.matcher(value).replaceAll("");
        return CONTINUATION_COMMENT.matcher(value).replaceAll("");
    }

    private static int[] codePointOffsets(String value) {
        int count = value.codePointCount(0, value.length());
        int[] offsets = new int[count + 1];
        int charOffset = 0;
        for (int codePoint = 0; codePoint < count; codePoint++) {
            offsets[codePoint] = charOffset;
            charOffset = value.offsetByCodePoints(charOffset, 1);
        }
        offsets[count] = value.length();
        return offsets;
    }

    private static String normalizeText(String value) {
        StringBuilder result = new StringBuilder();
        char quote = 0;
        boolean escaped = false;
        boolean pendingSpace = false;

        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);

            if (escaped) {
                result.append(current);
                escaped = false;
                continue;
            }
            if (current == GRAMMAR_ESCAPE_TOKEN && quote != '\'') {
                if (pendingSpace && result.length() > 0) {
                    result.append(' ');
                    pendingSpace = false;
                }
                result.append(current);
                escaped = true;
                continue;
            }
            if (quote != 0) {
                result.append(current);
                if (current == quote) {
                    quote = 0;
                }
                continue;
            }
            if (current == '\'' || current == '"') {
                if (pendingSpace && result.length() > 0) {
                    result.append(' ');
                    pendingSpace = false;
                }
                quote = current;
                result.append(current);
                continue;
            }
            if (Character.isWhitespace(current)) {
                pendingSpace = result.length() > 0;
                continue;
            }
            if (pendingSpace) {
                result.append(' ');
                pendingSpace = false;
            }
            result.append(current);
        }

        return result.toString();
    }

    private static String decodeBuilderFlag(String token) {
        String value = logicalTokenText(token);
        StringBuilder result = new StringBuilder();
        char quote = 0;
        boolean escaped = false;

        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (escaped) {
                result.append(current);
                escaped = false;
                continue;
            }
            if (current == GRAMMAR_ESCAPE_TOKEN) {
                escaped = true;
                continue;
            }
            if (quote != 0) {
                if (current == quote) {
                    quote = 0;
                } else {
                    result.append(current);
                }
                continue;
            }
            if (current == '\'' || current == '"') {
                quote = current;
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }

    private static String decodeString(String token) {
        if (token.length() < 2) {
            return token;
        }

        char quote = token.charAt(0);
        StringBuilder result = new StringBuilder();
        for (int index = 1; index < token.length() - 1; index++) {
            char current = token.charAt(index);
            if (current != '\\' || index + 1 >= token.length() - 1) {
                result.append(current);
                continue;
            }

            char escaped = token.charAt(++index);
            switch (escaped) {
                case '"':
                case '\'':
                case '\\':
                case '/':
                    result.append(escaped);
                    break;
                case 'b':
                    result.append('\b');
                    break;
                case 'f':
                    result.append('\f');
                    break;
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case 't':
                    result.append('\t');
                    break;
                case 'u':
                    if (index + 4 < token.length() - 1) {
                        String digits = token.substring(index + 1, index + 5);
                        try {
                            char decoded = (char) Integer.parseInt(digits, 16);
                            if (Character.isHighSurrogate(decoded)
                                && index + 10 < token.length() - 1
                                && token.charAt(index + 5) == '\\'
                                && token.charAt(index + 6) == 'u') {
                                String lowDigits = token.substring(index + 7, index + 11);
                                char low = (char) Integer.parseInt(lowDigits, 16);
                                if (Character.isLowSurrogate(low)) {
                                    result.appendCodePoint(Character.toCodePoint(decoded, low));
                                    index += 10;
                                    break;
                                }
                            }
                            result.append(Character.isSurrogate(decoded) ? '\uFFFD' : decoded);
                            index += 4;
                            break;
                        } catch (NumberFormatException ignored) {
                            // Preserve invalid escapes so the parity diff remains inspectable.
                        }
                    }
                    result.append('\\').append(escaped);
                    break;
                default:
                    result.append(escaped);
                    break;
            }
        }

        if (quote != '"' && quote != '\'') {
            return token;
        }
        return result.toString();
    }

    private static final class DiagnosticListener extends BaseErrorListener {
        private final String input;
        private final List<String> messages = new ArrayList<>();

        private DiagnosticListener(String input) {
            this.input = input;
        }

        @Override
        public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String message,
            RecognitionException error
        ) {
            messages.add(String.format(
                "%s:%d:%d: %s",
                input,
                line,
                charPositionInLine + 1,
                message
            ));
        }
    }

    private static final class Document {
        private final String escape = Character.toString(GRAMMAR_ESCAPE_TOKEN);
        private final List<Instruction> instructions = new ArrayList<>();
    }

    private static final class Instruction {
        private String command;
        private String argumentKind;
        private Object arguments;
        private final List<String> flags = new ArrayList<>();
        private final List<Instruction> children = new ArrayList<>();
        private final List<Heredoc> heredocs = new ArrayList<>();
        private Location location;
    }

    private static final class Heredoc {
        private String name;
        private long fileDescriptor;
        private boolean expand;
        private boolean chomp;
        private String content;
    }

    private static final class Location {
        private final int startLine;
        private final int endLine;

        private Location(int startLine, int endLine) {
            this.startLine = startLine;
            this.endLine = endLine;
        }
    }

    private static final class Json {
        private Json() {
        }

        private static String write(Document document) {
            StringBuilder result = new StringBuilder();
            result.append("{\n");
            field(result, 1, "escape", quote(document.escape), true);
            indent(result, 1).append("\"instructions\": ");
            instructions(result, document.instructions, 1);
            result.append('\n').append("}\n");
            return result.toString();
        }

        private static void instruction(StringBuilder result, Instruction instruction, int level) {
            result.append("{\n");
            field(result, level + 1, "command", quote(instruction.command), true);
            field(result, level + 1, "argumentKind", quote(instruction.argumentKind), true);
            indent(result, level + 1).append("\"arguments\": ");
            if (instruction.arguments instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) instruction.arguments;
                strings(result, values, level + 1);
            } else {
                result.append(quote((String) instruction.arguments));
            }
            result.append(",\n");
            indent(result, level + 1).append("\"flags\": ");
            strings(result, instruction.flags, level + 1);
            result.append(",\n");
            indent(result, level + 1).append("\"children\": ");
            instructions(result, instruction.children, level + 1);
            result.append(",\n");
            indent(result, level + 1).append("\"heredocs\": ");
            heredocs(result, instruction.heredocs, level + 1);
            result.append(",\n");
            indent(result, level + 1).append("\"location\": {\n");
            field(
                result,
                level + 2,
                "startLine",
                Integer.toString(instruction.location.startLine),
                true
            );
            field(
                result,
                level + 2,
                "endLine",
                Integer.toString(instruction.location.endLine),
                false
            );
            indent(result, level + 1).append("}\n");
            indent(result, level).append('}');
        }

        private static void instructions(
            StringBuilder result,
            List<Instruction> instructions,
            int level
        ) {
            if (instructions.isEmpty()) {
                result.append("[]");
                return;
            }
            result.append("[\n");
            for (int index = 0; index < instructions.size(); index++) {
                indent(result, level + 1);
                instruction(result, instructions.get(index), level + 1);
                result.append(index + 1 < instructions.size() ? ",\n" : "\n");
            }
            indent(result, level).append(']');
        }

        private static void strings(StringBuilder result, List<String> values, int level) {
            if (values.isEmpty()) {
                result.append("[]");
                return;
            }
            result.append("[\n");
            for (int index = 0; index < values.size(); index++) {
                indent(result, level + 1).append(quote(values.get(index)));
                result.append(index + 1 < values.size() ? ",\n" : "\n");
            }
            indent(result, level).append(']');
        }

        private static void heredocs(StringBuilder result, List<Heredoc> values, int level) {
            if (values.isEmpty()) {
                result.append("[]");
                return;
            }
            result.append("[\n");
            for (int index = 0; index < values.size(); index++) {
                Heredoc heredoc = values.get(index);
                indent(result, level + 1).append("{\n");
                field(result, level + 2, "name", quote(heredoc.name), true);
                field(
                    result,
                    level + 2,
                    "fileDescriptor",
                    Long.toString(heredoc.fileDescriptor),
                    true
                );
                field(result, level + 2, "expand", Boolean.toString(heredoc.expand), true);
                field(result, level + 2, "chomp", Boolean.toString(heredoc.chomp), true);
                field(result, level + 2, "content", quote(heredoc.content), false);
                indent(result, level + 1).append('}');
                result.append(index + 1 < values.size() ? ",\n" : "\n");
            }
            indent(result, level).append(']');
        }

        private static void field(
            StringBuilder result,
            int level,
            String name,
            String value,
            boolean comma
        ) {
            indent(result, level)
                .append(quote(name))
                .append(": ")
                .append(value)
                .append(comma ? ",\n" : "\n");
        }

        private static StringBuilder indent(StringBuilder result, int level) {
            return result.append("  ".repeat(level));
        }

        private static String quote(String value) {
            StringBuilder result = new StringBuilder("\"");
            for (int index = 0; index < value.length(); index++) {
                char current = value.charAt(index);
                switch (current) {
                    case '"':
                        result.append("\\\"");
                        break;
                    case '\\':
                        result.append("\\\\");
                        break;
                    case '\b':
                        result.append("\\b");
                        break;
                    case '\f':
                        result.append("\\f");
                        break;
                    case '\n':
                        result.append("\\n");
                        break;
                    case '\r':
                        result.append("\\r");
                        break;
                    case '\t':
                        result.append("\\t");
                        break;
                    default:
                        if (current < 0x20 || current == '\u2028' || current == '\u2029') {
                            result.append(String.format("\\u%04x", (int) current));
                        } else {
                            result.append(current);
                        }
                }
            }
            return result.append('"').toString();
        }
    }
}
