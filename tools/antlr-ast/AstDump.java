import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class AstDump {
    // Keep this projection deliberately literal. Values come from emitted tokens
    // and parse-tree structure; semantic gaps must be fixed in the grammar, not here.
    private AstDump() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: AstDump DOCKERFILE");
            System.exit(2);
        }

        Path input = Path.of(args[0]);
        CharStream source;
        try {
            source = CharStreams.fromPath(input, StandardCharsets.UTF_8);
        } catch (IOException error) {
            System.err.printf("%s: %s%n", input, error.getMessage());
            System.exit(2);
            return;
        }

        DiagnosticListener diagnostics = new DiagnosticListener(input.toString());
        DockerfileLexer lexer = new DockerfileLexer(source);
        lexer.removeErrorListeners();
        lexer.addErrorListener(diagnostics);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DockerfileParser parser = new DockerfileParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(diagnostics);

        DockerfileParser.DockerfileContext tree = parser.dockerfile();
        tokens.fill();

        if (!diagnostics.messages.isEmpty()) {
            diagnostics.messages.forEach(System.err::println);
            System.exit(1);
        }

        Document document = new Document(effectiveEscapeToken(tokens));
        for (DockerfileParser.ElementContext element : tree.element()) {
            if (element.instruction() != null) {
                document.instructions.add(toInstruction(element.instruction()));
            }
        }

        byte[] output = Json.write(document).getBytes(StandardCharsets.UTF_8);
        System.out.write(output, 0, output.length);
    }

    private static char effectiveEscapeToken(CommonTokenStream tokens) {
        for (Token token : tokens.getTokens()) {
            if (token.getType() == DockerfileLexer.BACKTICK_ESCAPE_DIRECTIVE) {
                return '`';
            }
            if (token.getType() == DockerfileLexer.BACKSLASH_ESCAPE_DIRECTIVE) {
                return '\\';
            }
        }
        return '\\';
    }

    private static Instruction toInstruction(DockerfileParser.InstructionContext wrapper) {
        ParseTree firstChild = wrapper.getChild(0);
        if (!(firstChild instanceof ParserRuleContext)) {
            throw new IllegalStateException("instruction has no parser-rule child");
        }

        ParserRuleContext context = (ParserRuleContext) firstChild;
        Token commandToken = context.getStart();

        Instruction instruction = new Instruction();
        instruction.command = commandToken.getText();
        instruction.location = new Location(
            commandToken.getLine(),
            context.getStop().getLine()
        );

        DockerfileParser.Argument_preambleContext preamble = directChild(
            context,
            DockerfileParser.Argument_preambleContext.class
        );
        DockerfileParser.Builder_flagsContext builderFlags = preamble == null
            ? null
            : directChild(preamble, DockerfileParser.Builder_flagsContext.class);
        if (builderFlags != null) {
            for (DockerfileParser.Builder_flagContext flag :
                builderFlags.builder_flag()) {
                instruction.flags.add(flag.getText());
            }
        }

        DockerfileParser.Json_arrayContext json = directChild(
            context,
            DockerfileParser.Json_arrayContext.class
        );
        DockerfileParser.Healthcheck_commandContext healthcheck = directChild(
            context,
            DockerfileParser.Healthcheck_commandContext.class
        );
        if (json == null && healthcheck != null) {
            json = directChild(healthcheck, DockerfileParser.Json_arrayContext.class);
        }
        instruction.argumentKind = json == null ? "text" : "json";
        addDirectArguments(context, commandToken, instruction.arguments);

        if (context.children != null) {
            for (ParseTree child : context.children) {
                if (child instanceof DockerfileParser.InstructionContext) {
                    instruction.children.add(
                        toInstruction((DockerfileParser.InstructionContext) child)
                    );
                }
            }
        }

        return instruction;
    }

    private static void addDirectArguments(
        ParserRuleContext context,
        Token command,
        List<String> arguments
    ) {
        if (context.children == null) {
            return;
        }
        for (ParseTree child : context.children) {
            if (child instanceof DockerfileParser.Shell_argumentContext) {
                arguments.add(child.getText());
            } else if (child instanceof DockerfileParser.Argument_listContext) {
                addArgumentList(
                    (DockerfileParser.Argument_listContext) child,
                    arguments
                );
            } else if (child instanceof DockerfileParser.Word_listContext) {
                addWordList(
                    (DockerfileParser.Word_listContext) child,
                    arguments
                );
            } else if (child instanceof DockerfileParser.Name_value_argumentsContext) {
                addNameValueArguments(
                    (DockerfileParser.Name_value_argumentsContext) child,
                    arguments
                );
            } else if (child instanceof DockerfileParser.Healthcheck_commandContext) {
                addHealthcheckArguments(
                    (DockerfileParser.Healthcheck_commandContext) child,
                    arguments
                );
            } else if (child instanceof DockerfileParser.Json_arrayContext) {
                addJsonArguments(
                    (DockerfileParser.Json_arrayContext) child,
                    arguments
                );
            } else if (child instanceof TerminalNode) {
                Token token = ((TerminalNode) child).getSymbol();
                if (token != command && token.getType() == DockerfileLexer.NONE) {
                    arguments.add(token.getText());
                }
            }
        }
    }

    private static void addArgumentList(
        DockerfileParser.Argument_listContext context,
        List<String> values
    ) {
        for (DockerfileParser.List_argumentContext argument :
            context.list_argument()) {
            values.add(argument.getText());
        }
    }

    private static void addWordList(
        DockerfileParser.Word_listContext context,
        List<String> values
    ) {
        for (DockerfileParser.Argument_wordContext word : context.argument_word()) {
            values.add(word.getText());
        }
    }

    private static void addNameValueArguments(
        DockerfileParser.Name_value_argumentsContext context,
        List<String> values
    ) {
        List<DockerfileParser.Name_value_pairContext> pairs =
            context.name_value_pair();
        if (!pairs.isEmpty()) {
            for (DockerfileParser.Name_value_pairContext pair : pairs) {
                values.add(pair.argument_name().getText());
                DockerfileParser.Assignment_valueContext value =
                    pair.assignment_value();
                values.add(value == null ? "" : value.getText());
                values.add(pair.EQUALS().getText());
            }
            return;
        }

        values.add(context.argument_name().getText());
        values.add(context.shell_argument().getText());
        values.add("");
    }

    private static void addHealthcheckArguments(
        DockerfileParser.Healthcheck_commandContext context,
        List<String> values
    ) {
        values.add(context.CMD().getText());
        DockerfileParser.Json_arrayContext json = context.json_array();
        if (json != null) {
            addJsonArguments(json, values);
        } else if (context.shell_argument() != null) {
            values.add(context.shell_argument().getText());
        }
    }

    private static void addJsonArguments(
        DockerfileParser.Json_arrayContext context,
        List<String> values
    ) {
        for (DockerfileParser.Json_string_valueContext string :
            context.json_string_value()) {
            values.add(string.getText());
        }
    }

    private static <T extends ParseTree> T directChild(
        ParserRuleContext context,
        Class<T> type
    ) {
        if (context.children == null) {
            return null;
        }
        for (ParseTree child : context.children) {
            if (type.isInstance(child)) {
                return type.cast(child);
            }
        }
        return null;
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
        private final String escape;
        private final List<Instruction> instructions = new ArrayList<>();

        private Document(char escape) {
            this.escape = Character.toString(escape);
        }
    }

    private static final class Instruction {
        private String command;
        private String argumentKind;
        private final List<String> arguments = new ArrayList<>();
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
            strings(result, instruction.arguments, level + 1);
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
