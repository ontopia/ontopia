/**
 * TMQL4J Plugin for Ontopia
 * 
 * Copyright: Copyright 2009 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Author: Sven Krosse
 * 
 */
package net.ontopia.topicmaps.query.tmql.impl.tmql4jextension;

import de.topicmapslab.tmql4j.common.core.exception.TMQLRuntimeException;
import de.topicmapslab.tmql4j.common.core.process.TMQLRuntime;
import de.topicmapslab.tmql4j.common.model.process.IInitializationTask;
import de.topicmapslab.tmql4j.common.model.process.IProcessingTask;
import de.topicmapslab.tmql4j.common.model.process.IProcessingTaskChain;
import de.topicmapslab.tmql4j.common.model.query.IQuery;
import de.topicmapslab.tmql4j.lexer.base.LexerTask;
import de.topicmapslab.tmql4j.parser.base.ParsingTask;
import de.topicmapslab.tmql4j.parser.model.IParserTree;
import de.topicmapslab.tmql4j.preprocessing.base.CanonizerTask;
import de.topicmapslab.tmql4j.preprocessing.base.InitialisingTask;
import de.topicmapslab.tmql4j.preprocessing.base.ScreenerTask;
import de.topicmapslab.tmql4j.preprocessing.base.WhiteSpacerTask;

public class OntopiaParserProcessingTaskChain implements IProcessingTaskChain {

	private final TMQLRuntime runtime;
	private final IQuery query;

	public OntopiaParserProcessingTaskChain(final TMQLRuntime runtime, final IQuery query) {
		this.runtime = runtime;
		this.query = query;
	}

	public void execute() throws TMQLRuntimeException {
		IProcessingTask task = null;
		/*
		 * Initialization
		 */
		task = new InitialisingTask(runtime.getTopicMapSystem(), runtime
				.getTopicMaps());
		task.init();
		task.run();
		runtime.setInitialContext(((IInitializationTask) task)
				.getInitialContext());

		long t = System.currentTimeMillis();

		/*
		 * Screener
		 */
		task = new ScreenerTask(query);
		task.init();
		task.run();
		runtime.store(TMQLRuntime.TMQL_RUNTIME_QUERY_SCREENED,
				((ScreenerTask) task).getResult());

		System.out.println(System.currentTimeMillis() - t);
		t = System.currentTimeMillis();
		/*
		 * WhiteSpacer
		 */

		task = new WhiteSpacerTask(((ScreenerTask) task).getResult());
		task.init();
		task.run();
		runtime.store(TMQLRuntime.TMQL_RUNTIME_QUERY_WHITESPACED,
				((WhiteSpacerTask) task).getResult());

		/*
		 * Canonizer
		 */
		task = new CanonizerTask(((WhiteSpacerTask) task).getResult());
		task.init();
		task.run();
		runtime.store(TMQLRuntime.TMQL_RUNTIME_QUERY_CANONIZED,
				((CanonizerTask) task).getResult());

		System.out.println(System.currentTimeMillis() - t);
		t = System.currentTimeMillis();

		runtime.getRuntimeContext().pushToStack(
				runtime.getInitialContext().getPredefinedVariableSet()
						.copyWithValues());

		/*
		 * Lexer
		 */
		task = new LexerTask(((CanonizerTask) task).getResult());
		task.init();
		task.run();
		runtime.store(TMQLRuntime.TMQL_RUNTIME_LEXER_CHAIN, ((LexerTask) task)
				.getResult().getChain());

		System.out.println(System.currentTimeMillis() - t);
		t = System.currentTimeMillis();

		/*
		 * Parser
		 */
		task = new ParsingTask(((LexerTask) task).getResult(), runtime);
		task.init();
		task.run();
		runtime.store(TMQLRuntime.TMQL_RUNTIME_PARSER_TREE,
				((ParsingTask) task).getResult().getParserTree());
	}

	public TMQLRuntime getTmqlRuntime() {
		return runtime;
	}

	public IParserTree getParserTree() {
		return runtime.getParserTree();
	}

}
