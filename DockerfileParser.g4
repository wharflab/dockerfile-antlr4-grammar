parser grammar DockerfileParser;

options { tokenVocab=DockerfileLexer; }

dockerfile: (element)* EOF;

element
    : instruction
    | comment
    | NL
    ;

comment: CHASH COMMENT_TEXT? NL;

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

from_inst: FROM arguments NL;
run_inst: RUN (json_array | arguments) NL;
cmd_inst: CMD (json_array | arguments) NL;
label_inst: LABEL arguments NL;
expose_inst: EXPOSE arguments NL;
env_inst: ENV arguments NL;
add_inst: ADD (json_array | arguments) NL;
copy_inst: COPY (json_array | arguments) NL;
entrypoint_inst: ENTRYPOINT (json_array | arguments) NL;
volume_inst: VOLUME (json_array | arguments) NL;
user_inst: USER arguments NL;
workdir_inst: WORKDIR arguments NL;
arg_inst: ARG arguments NL;
onbuild_inst: ONBUILD (instruction | NL);
stopsignal_inst: STOPSIGNAL arguments NL;
healthcheck_inst
    : HEALTHCHECK NONE NL
    | HEALTHCHECK arguments? instruction
    ;
shell_inst: SHELL json_array NL;

json_array: LBRACKET (STRING (COMMA STRING)*)? RBRACKET;

arguments: (ARG_TEXT | STRING | LBRACKET | RBRACKET | COMMA | NONE | CMD)+;
