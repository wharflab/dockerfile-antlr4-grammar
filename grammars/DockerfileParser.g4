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
    : FROM argument_preamble argument_list NL
    ;
run_inst
    : RUN argument_preamble (json_array | shell_argument)? NL
    ;
cmd_inst: CMD argument_preamble (json_array | shell_argument) NL;
label_inst: LABEL argument_preamble name_value_arguments NL;
expose_inst: EXPOSE argument_preamble argument_list NL;
env_inst: ENV argument_preamble name_value_arguments NL;
add_inst
    : ADD argument_preamble (json_array | argument_list) NL
    ;
copy_inst
    : COPY argument_preamble (json_array | argument_list) NL
    ;
entrypoint_inst
    : ENTRYPOINT argument_preamble (json_array | shell_argument) NL
    ;
volume_inst: VOLUME argument_preamble (json_array | argument_list) NL;
user_inst: USER argument_preamble shell_argument NL;
workdir_inst: WORKDIR argument_preamble shell_argument NL;
arg_inst: ARG argument_preamble word_list NL;
onbuild_inst: ONBUILD (instruction | NL);
stopsignal_inst: STOPSIGNAL argument_preamble shell_argument NL;
healthcheck_inst
    : HEALTHCHECK argument_preamble NONE NL
    | HEALTHCHECK argument_preamble healthcheck_command NL
    ;
shell_inst: SHELL argument_preamble json_array NL;

healthcheck_command
    : CMD (ARG_WS+ (json_array | shell_argument))?
    ;

argument_preamble
    : ARG_WS+ builder_flags ARG_WS*
    | ARG_WS+
    ;

builder_flags
    : builder_flag (
        ARG_WS+ builder_flag
      )* (
        ARG_WS+ BUILDER_FLAG_TERMINATOR
      )?
    | BUILDER_FLAG_TERMINATOR
    ;

builder_flag
    : BUILDER_FLAG_START BUILDER_FLAG_TEXT+
    ;

json_array
    : LBRACKET ARG_WS* (
        json_string_value ARG_WS* (
            COMMA ARG_WS* json_string_value ARG_WS*
        )*
      )? RBRACKET
    ;

json_string_value
    : JSON_STRING_START (
        JSON_STRING_TEXT
        | JSON_STRING_SPACE
        | JSON_STRING_ESCAPE
      )* JSON_STRING_END
    ;

string_value
    : JSON_STRING_START double_string_atom* JSON_STRING_END
    | STRING_START (STRING_TEXT | ARG_WS | ESCAPE)* STRING_END
    ;

unterminated_string_value
    : JSON_STRING_START double_string_atom*
    | STRING_START (STRING_TEXT | ARG_WS | ESCAPE)*
    ;

argument_string_value
    : string_value
    | unterminated_string_value
    ;

double_string_atom
    : JSON_STRING_TEXT
    | JSON_STRING_SPACE
    | JSON_STRING_ESCAPE
    | STRING_TEXT
    | ESCAPE
    | ARG_WS
    ;

// BuildKit keeps shell-form text as one node, but splits list-form arguments
// on raw whitespace even when that whitespace appears inside quotes.
shell_argument
    : (list_atom | list_whitespace)+
    ;

argument_list
    : list_argument (list_whitespace+ list_argument)*
    ;

list_argument
    : list_atom+
    ;

list_whitespace
    : ARG_WS
    | JSON_STRING_SPACE
    ;

word_list
    : argument_word (ARG_WS+ argument_word)*
    ;

// ARG, ENV, and LABEL use quote- and escape-aware words instead of raw splits.
argument_word
    : argument_atom+
    ;

name_value_arguments
    : name_value_pair (ARG_WS+ name_value_pair)*
    | argument_name ARG_WS+ shell_argument
    ;

name_value_pair
    : argument_name EQUALS assignment_value?
    ;

argument_name
    : word_atom+
    ;

assignment_value
    : argument_word
    ;

argument_atom
    : word_atom
    | EQUALS
    ;

word_atom
    : ARG_TEXT
    | BUILDER_FLAG
    | BUILDER_FLAG_TERMINATOR
    | argument_string_value
    | escaped_whitespace
    | LBRACKET
    | RBRACKET
    | COMMA
    | NONE
    | CMD
    ;

escaped_whitespace
    : ESCAPE ARG_WS
    ;

list_atom
    : ARG_TEXT
    | BUILDER_FLAG
    | BUILDER_FLAG_TERMINATOR
    | STRING_START
    | STRING_TEXT
    | STRING_END
    | JSON_STRING_START
    | JSON_STRING_TEXT
    | JSON_STRING_ESCAPE
    | JSON_STRING_END
    | ESCAPE
    | LBRACKET
    | RBRACKET
    | COMMA
    | EQUALS
    | NONE
    | CMD
    ;
