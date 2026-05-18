lexer grammar DockerfileLexer;

// Keywords at the start of a line (DEFAULT_MODE)
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

CHASH: '#' -> mode(MODE_COMMENT);

NL: ( '\r'? '\n' | '\r' )+;
WS: [ \t]+ -> skip;

mode MODE_COMMENT;
    COMMENT_TEXT: ~[\r\n]+;
    COMMENT_NL: ( '\r'? '\n' | '\r' ) -> mode(DEFAULT_MODE), type(NL);

mode MODE_ARGS;
    LINE_CONT: '\\' [ \t]* ( '\r'? '\n' | '\r' ) -> skip;
    
    LBRACKET: '[';
    RBRACKET: ']';
    COMMA: ',';
    STRING: '"' ( ~["\\] | '\\' . )* '"' | '\'' ( ~['\\] | '\\' . )* '\'';
    
    NONE: [nN][oO][nN][eE];
    CMD_IN_ARGS: [cC][mM][dD] -> type(CMD);
    
    ARG_TEXT: ~[\r\n \t[\] ,"'#]+;
    ARG_WS: [ \t]+ -> skip;
    
    ARG_HASH: '#' -> type(ARG_TEXT);

    ARGS_NL: ( '\r'? '\n' | '\r' ) -> mode(DEFAULT_MODE), type(NL);
