package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.agent.core.task.processor.kernel.qa.MockQaService;
import com.dianping.phoenix.configure.ConfigManager;

public class DefaultDeployStepTestConfigurator extends AbstractResourceConfigurator {

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ConfigManager.class));
		all.add(C(MockQaService.class));
		all.add(C(DefaultDeployStep.class).req(ConfigManager.class) //
				.req(MockQaService.class).is(PER_LOOKUP));
		
		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return DefaultDeployStepTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new DefaultDeployStepTestConfigurator());
	}

}
