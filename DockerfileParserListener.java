// Generated from DockerfileParser.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DockerfileParser}.
 */
public interface DockerfileParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#dockerfile}.
	 * @param ctx the parse tree
	 */
	void enterDockerfile(DockerfileParser.DockerfileContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#dockerfile}.
	 * @param ctx the parse tree
	 */
	void exitDockerfile(DockerfileParser.DockerfileContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(DockerfileParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(DockerfileParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(DockerfileParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(DockerfileParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#from_inst}.
	 * @param ctx the parse tree
	 */
	void enterFrom_inst(DockerfileParser.From_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#from_inst}.
	 * @param ctx the parse tree
	 */
	void exitFrom_inst(DockerfileParser.From_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#run_inst}.
	 * @param ctx the parse tree
	 */
	void enterRun_inst(DockerfileParser.Run_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#run_inst}.
	 * @param ctx the parse tree
	 */
	void exitRun_inst(DockerfileParser.Run_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#cmd_inst}.
	 * @param ctx the parse tree
	 */
	void enterCmd_inst(DockerfileParser.Cmd_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#cmd_inst}.
	 * @param ctx the parse tree
	 */
	void exitCmd_inst(DockerfileParser.Cmd_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#label_inst}.
	 * @param ctx the parse tree
	 */
	void enterLabel_inst(DockerfileParser.Label_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#label_inst}.
	 * @param ctx the parse tree
	 */
	void exitLabel_inst(DockerfileParser.Label_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#expose_inst}.
	 * @param ctx the parse tree
	 */
	void enterExpose_inst(DockerfileParser.Expose_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#expose_inst}.
	 * @param ctx the parse tree
	 */
	void exitExpose_inst(DockerfileParser.Expose_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#env_inst}.
	 * @param ctx the parse tree
	 */
	void enterEnv_inst(DockerfileParser.Env_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#env_inst}.
	 * @param ctx the parse tree
	 */
	void exitEnv_inst(DockerfileParser.Env_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#add_inst}.
	 * @param ctx the parse tree
	 */
	void enterAdd_inst(DockerfileParser.Add_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#add_inst}.
	 * @param ctx the parse tree
	 */
	void exitAdd_inst(DockerfileParser.Add_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#copy_inst}.
	 * @param ctx the parse tree
	 */
	void enterCopy_inst(DockerfileParser.Copy_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#copy_inst}.
	 * @param ctx the parse tree
	 */
	void exitCopy_inst(DockerfileParser.Copy_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#entrypoint_inst}.
	 * @param ctx the parse tree
	 */
	void enterEntrypoint_inst(DockerfileParser.Entrypoint_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#entrypoint_inst}.
	 * @param ctx the parse tree
	 */
	void exitEntrypoint_inst(DockerfileParser.Entrypoint_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#volume_inst}.
	 * @param ctx the parse tree
	 */
	void enterVolume_inst(DockerfileParser.Volume_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#volume_inst}.
	 * @param ctx the parse tree
	 */
	void exitVolume_inst(DockerfileParser.Volume_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#user_inst}.
	 * @param ctx the parse tree
	 */
	void enterUser_inst(DockerfileParser.User_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#user_inst}.
	 * @param ctx the parse tree
	 */
	void exitUser_inst(DockerfileParser.User_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#workdir_inst}.
	 * @param ctx the parse tree
	 */
	void enterWorkdir_inst(DockerfileParser.Workdir_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#workdir_inst}.
	 * @param ctx the parse tree
	 */
	void exitWorkdir_inst(DockerfileParser.Workdir_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#arg_inst}.
	 * @param ctx the parse tree
	 */
	void enterArg_inst(DockerfileParser.Arg_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#arg_inst}.
	 * @param ctx the parse tree
	 */
	void exitArg_inst(DockerfileParser.Arg_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#onbuild_inst}.
	 * @param ctx the parse tree
	 */
	void enterOnbuild_inst(DockerfileParser.Onbuild_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#onbuild_inst}.
	 * @param ctx the parse tree
	 */
	void exitOnbuild_inst(DockerfileParser.Onbuild_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#stopsignal_inst}.
	 * @param ctx the parse tree
	 */
	void enterStopsignal_inst(DockerfileParser.Stopsignal_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#stopsignal_inst}.
	 * @param ctx the parse tree
	 */
	void exitStopsignal_inst(DockerfileParser.Stopsignal_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#healthcheck_inst}.
	 * @param ctx the parse tree
	 */
	void enterHealthcheck_inst(DockerfileParser.Healthcheck_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#healthcheck_inst}.
	 * @param ctx the parse tree
	 */
	void exitHealthcheck_inst(DockerfileParser.Healthcheck_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#shell_inst}.
	 * @param ctx the parse tree
	 */
	void enterShell_inst(DockerfileParser.Shell_instContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#shell_inst}.
	 * @param ctx the parse tree
	 */
	void exitShell_inst(DockerfileParser.Shell_instContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#json_array}.
	 * @param ctx the parse tree
	 */
	void enterJson_array(DockerfileParser.Json_arrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#json_array}.
	 * @param ctx the parse tree
	 */
	void exitJson_array(DockerfileParser.Json_arrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerfileParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(DockerfileParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerfileParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(DockerfileParser.ArgumentsContext ctx);
}