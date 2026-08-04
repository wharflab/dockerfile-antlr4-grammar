lexer grammar DockerfileLexer;

// DEFAULT_MODE: Recognition of instruction keywords and comments at line start

FROM: [fF][rR][oO][mM] -> mode(MODE_ARGS);
RUN: [rR][uU][nN] -> mode(MODE_ARGS);
CMD: [cC][mM][dD] -> mode(MODE_ARGS);
LABEL: [lL][aA][bB][eE][lL] -> mode(MODE_ARGS);
EXPOSE: [eE][xX][pP][oO][sS][eE] -> mode(MODE_ARGS);
ENV: [eE][nN][vV] -> mode(MODE_ARGS);
ADD: [aA][dD][dD] -> mode(MODE_ARGS);
COPY: [cC][oO][pP][yY] -> mode(MODE_ARGS);
ENTRYPOINT: [eE][nN][tT][rR][yY][pP][oO][iI][nN][tT] -> mode(MODE_ARGS);
VOLUME: [vV][oO][lL][uU][mM][eE] -> mode(MODE_ARGS);
USER: [uU][sS][eE][rR] -> mode(MODE_ARGS);
WORKDIR: [wW][oO][rR][kK][dD][iI][rR] -> mode(MODE_ARGS);
ARG: [aA][rR][gG] -> mode(MODE_ARGS);
ONBUILD: [oO][nN][bB][uU][iI][lL][dD] -> mode(DEFAULT_MODE);
STOPSIGNAL: [sS][tT][oO][pP][sS][iI][gG][nN][aA][lL] -> mode(MODE_ARGS);
HEALTHCHECK: [hH][eE][aA][lL][tT][hH][cC][hH][eE][cC][kK] -> mode(MODE_ARGS);
SHELL: [sS][hH][eE][lL][lL] -> mode(MODE_ARGS);

// Top-level comments
COMMENT: [ \t]* '#' ~[\r\n]* -> skip;

NL: ( '\r'? '\n' | '\r' )+;
WS: [ \t]+ -> skip;

mode MODE_ARGS;
    // 1. Line continuation: \ followed by optional whitespace and newline
    ARG_LINE_CONT: '\\' [ \t]* ( '\r'? '\n' | '\r' ) -> skip;

    // 2. Comments inside instructions: only at the start of a line
    // We match the newline of the previous line IF it wasn't escaped,
    // OR we rely on the fact that if it was escaped, position is 0.
    // Actually, if it was escaped, it was skipped, so position IS 0.
    // If it WASN'T escaped, then ARG_NL would have triggered.
    // So this rule only matches if the previous line ended with \.
    ARG_COMMENT: { getCharPositionInLine() == 0 }? [ \t]* '#' ~[\r\n]* ( '\r'? '\n' | '\r' ) -> skip;

    // 3. Newline: actual end of instruction
    ARG_NL: [ \t]* ( '\r'? '\n' | '\r' ) -> mode(DEFAULT_MODE), type(NL);

    // 4. Leading builder flags (parsed structurally by instructions that support them)
    BUILDER_FLAG: '--' BUILDER_FLAG_PART+;
    BUILDER_FLAG_TERMINATOR: '--';

    fragment BUILDER_FLAG_PART
        : ~[\r\n \t"'\\]
        | BUILDER_FLAG_ESCAPE
        | '"' ( ~["\\\r\n] | BUILDER_FLAG_ESCAPE )* '"'
        | '\'' ( ~['\\\r\n] | BUILDER_FLAG_ESCAPE )* '\''
        ;

    fragment BUILDER_FLAG_ESCAPE
        : '\\' ( [ \t]* ( '\r'? '\n' | '\r' ) | . )?
        ;

    // 5. JSON-like structures for exec form
    LBRACKET: '[';
    RBRACKET: ']';
    COMMA: ',';
    
    // 6. Strings
    STRING: '"' ( ~["\\] | '\\' . )* '"' | '\'' ( ~['\\] | '\\' . )* '\'';
    
    // 7. Keywords that might appear in arguments (e.g. CMD in HEALTHCHECK)
    // We reuse the token types from DEFAULT_MODE if they appear here.
    ARG_NONE: [nN][oO][nN][eE] -> type(NONE);
    ARG_CMD: [cC][mM][dD] -> type(CMD);

    // 8. General text
    // Greedy match for most characters. Stop at anything that might have special meaning.
    ARG_TEXT: ~[\r\n \t[\] ,"'#\\]+;
    
    // Whitespace within arguments
    ARG_WS: [ \t]+ -> skip;

    // 9. Fallbacks for single special characters
    ARG_HASH: '#' -> type(ARG_TEXT);
    ARG_BACKSLASH: '\\' -> type(ARG_TEXT);
    ANY_OTHER: . -> type(ARG_TEXT);

// Token types for reuse
NONE: [nN][oO][nN][eE];
