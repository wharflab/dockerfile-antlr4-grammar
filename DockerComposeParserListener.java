// Generated from DockerComposeParser.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DockerComposeParser}.
 */
public interface DockerComposeParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#composeFile}.
	 * @param ctx the parse tree
	 */
	void enterComposeFile(DockerComposeParser.ComposeFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#composeFile}.
	 * @param ctx the parse tree
	 */
	void exitComposeFile(DockerComposeParser.ComposeFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(DockerComposeParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(DockerComposeParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(DockerComposeParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(DockerComposeParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#key}.
	 * @param ctx the parse tree
	 */
	void enterKey(DockerComposeParser.KeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#key}.
	 * @param ctx the parse tree
	 */
	void exitKey(DockerComposeParser.KeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(DockerComposeParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(DockerComposeParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#nested_block}.
	 * @param ctx the parse tree
	 */
	void enterNested_block(DockerComposeParser.Nested_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#nested_block}.
	 * @param ctx the parse tree
	 */
	void exitNested_block(DockerComposeParser.Nested_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#listItem}.
	 * @param ctx the parse tree
	 */
	void enterListItem(DockerComposeParser.ListItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#listItem}.
	 * @param ctx the parse tree
	 */
	void exitListItem(DockerComposeParser.ListItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link DockerComposeParser#flow_list}.
	 * @param ctx the parse tree
	 */
	void enterFlow_list(DockerComposeParser.Flow_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link DockerComposeParser#flow_list}.
	 * @param ctx the parse tree
	 */
	void exitFlow_list(DockerComposeParser.Flow_listContext ctx);
}