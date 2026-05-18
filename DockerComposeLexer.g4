lexer grammar DockerComposeLexer;

@header {
    import org.antlr.v4.runtime.*;
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
        CommonToken t = new CommonToken(type, text);
        t.setLine(parent.getLine());
        t.setCharPositionInLine(parent.getCharPositionInLine());
        t.setStartIndex(parent.getStartIndex());
        t.setStopIndex(parent.getStopIndex());
        return t;
    }
}

COLON: ':' { Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF }?;
DASH: '-' { Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF }?;
LBRACKET: '[';
RBRACKET: ']';
COMMA: ',';

STRING: '"' ( ~["\\] | '\\' . )* '"' | '\'' ( ~['\\] | '\\' . )* '\'';

// SCALAR: Match anything that is not one of the separators
SCALAR: (~[ \t\r\n:#[\],\-]|(':' ~[ \t\r\n])|('-' ~[ \t\r\n]))+;

COMMENT: '#' ~[\r\n]* -> skip;

WS: [ \t]+ -> channel(HIDDEN);
NEWLINE: ( '\r'? '\n' | '\r' )+;

INDENT: { false }? 'INDENT';
DEDENT: { false }? 'DEDENT';

// Fallback for when COLON/DASH predicates fail (they should be part of SCALAR)
// But since SCALAR is greedy and comes later, we might need a catch-all if SCALAR misses something.
ANY_CHAR: . -> type(SCALAR);
