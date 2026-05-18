lexer grammar DockerComposeLexer;

@header {
    import org.antlr.v4.runtime.*;
    import org.antlr.v4.runtime.misc.Pair;
    import java.util.*;
}

@members {
    private Deque<Integer> indentStack = new ArrayDeque<>();
    private Queue<Token> tokenQueue = new LinkedList<>();
    private boolean opened = false;

    @Override
    public Token nextToken() {
        if (!opened) {
            indentStack.push(0);
            opened = true;
        }

        if (!tokenQueue.isEmpty()) {
            return tokenQueue.poll();
        }

        Token t = super.nextToken();

        if (t.getType() == NEWLINE) {
            Token next = super.nextToken();
            int indent = 0;
            while (next.getType() == WS || next.getType() == NEWLINE) {
                if (next.getType() == NEWLINE) {
                    t = next;
                    next = super.nextToken();
                } else {
                    indent = next.getText().length();
                    next = super.nextToken();
                }
            }
            
            if (next.getType() == EOF) {
                indent = 0;
            }

            int prevIndent = indentStack.peek();
            if (indent > prevIndent) {
                indentStack.push(indent);
                tokenQueue.add(createToken(INDENT, "INDENT", t));
            } else if (indent < prevIndent) {
                while (!indentStack.isEmpty() && indentStack.peek() > indent) {
                    indentStack.pop();
                    tokenQueue.add(createToken(DEDENT, "DEDENT", t));
                }
            }
            
            tokenQueue.add(next);
            return t;
        } else if (t.getType() == EOF) {
            while (!indentStack.isEmpty() && indentStack.peek() > 0) {
                indentStack.pop();
                tokenQueue.add(createToken(DEDENT, "DEDENT", t));
            }
            tokenQueue.add(t);
            return tokenQueue.poll();
        }

        return t;
    }

    private Token createToken(int type, String text, Token parent) {
        Pair<TokenSource, CharStream> source = new Pair<>(this, _input);
        CommonToken t = new CommonToken(source, type, parent.getChannel(), parent.getStartIndex(), parent.getStopIndex());
        t.setText(text);
        t.setLine(parent.getLine());
        t.setCharPositionInLine(parent.getCharPositionInLine());
        return t;
    }
}

COLON: ':' { Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF || _input.LA(1) == '\r' || _input.LA(1) == '\n' }?;
DASH: '-' { Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF || _input.LA(1) == '\r' || _input.LA(1) == '\n' }?;
LBRACKET: '[';
RBRACKET: ']';
COMMA: ',' { Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF || _input.LA(1) == '\r' || _input.LA(1) == '\n' }?;

STRING: '"' ( ~["\\] | '\\' . )* '"' | '\'' ( ~['\\] | '\\' . )* '\'';

SCALAR: (~[ \t\r\n#[\],\-]|(':' ~[ \t\r\n])|('-' ~[ \t\r\n])|(',' ~[ \t\r\n]))+;

COMMENT: '#' ~[\r\n]* -> skip;

WS: [ \t]+ -> channel(HIDDEN);
NEWLINE: ( '\r'? '\n' | '\r' )+;

INDENT: { false }? 'INDENT';
DEDENT: { false }? 'DEDENT';

ANY_CHAR: . -> type(SCALAR);
