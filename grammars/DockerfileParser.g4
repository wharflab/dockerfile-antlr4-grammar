parser grammar DockerfileParser;

options { tokenVocab=DockerfileLexer; }

dockerfile: parser_directives? (element)* EOF;

parser_directives
    : SYNTAX_DIRECTIVE (
        escape_directive CHECK_DIRECTIVE?
        | CHECK_DIRECTIVE escape_directive?
      )?
    | escape_directive (
        SYNTAX_DIRECTIVE CHECK_DIRECTIVE?
        | CHECK_DIRECTIVE SYNTAX_DIRECTIVE?
      )?
    | CHECK_DIRECTIVE (
        SYNTAX_DIRECTIVE escape_directive?
        | escape_directive SYNTAX_DIRECTIVE?
      )?
    ;

escape_directive
    : BACKTICK_ESCAPE_DIRECTIVE
    | BACKSLASH_ESCAPE_DIRECTIVE
    ;

element
    : instruction
    | NL
    ;

// Top-level comments are skipped by the lexer

instruction
    : from_inst
    | run_inst
    | cmd_inst
    | label_inst
    | expose_inst
    | env_inst
    | add_inst
    | copy_inst
    | entrypoint_inst
    | volume_inst
    | user_inst
    | workdir_inst
    | arg_inst
    | onbuild_inst
    | stopsignal_inst
    | healthcheck_inst
    | shell_inst
    ;

from_inst
    : FROM builder_flags arguments NL
    | FROM arguments NL
    ;
run_inst
    : RUN builder_flags (json_array | arguments)? NL
    | RUN (json_array | arguments) NL
    ;
cmd_inst: CMD (json_array | arguments) NL;
label_inst: LABEL arguments NL;
expose_inst: EXPOSE arguments NL;
env_inst: ENV arguments NL;
add_inst
    : ADD builder_flags (json_array | arguments) NL
    | ADD (json_array | arguments) NL
    ;
copy_inst
    : COPY builder_flags (json_array | arguments) NL
    | COPY (json_array | arguments) NL
    ;
entrypoint_inst: ENTRYPOINT (json_array | arguments) NL;
volume_inst: VOLUME (json_array | arguments) NL;
user_inst: USER arguments NL;
workdir_inst: WORKDIR arguments NL;
arg_inst: ARG arguments NL;
onbuild_inst: ONBUILD (instruction | NL);
stopsignal_inst: STOPSIGNAL arguments NL;
healthcheck_inst
    : HEALTHCHECK builder_flags NONE NL
    | HEALTHCHECK NONE NL
    | HEALTHCHECK builder_flags arguments? instruction
    | HEALTHCHECK arguments? instruction
    ;
shell_inst: SHELL json_array NL;

builder_flags
    : BUILDER_FLAG+ BUILDER_FLAG_TERMINATOR?
    | BUILDER_FLAG_TERMINATOR
    ;

json_array
    : LBRACKET (string_value (COMMA string_value)*)? RBRACKET
    ;

string_value
    : STRING
    | STRING_START STRING_TEXT* STRING_END
    ;

arguments
    : (
        ARG_TEXT
        | BUILDER_FLAG
        | BUILDER_FLAG_TERMINATOR
        | string_value
        | LBRACKET
        | RBRACKET
        | COMMA
        | NONE
        | CMD
      )+
    ;
