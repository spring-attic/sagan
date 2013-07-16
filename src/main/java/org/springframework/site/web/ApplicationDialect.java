package org.springframework.site.web;

import org.thymeleaf.Arguments;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.util.HashSet;
import java.util.Set;

public class ApplicationDialect extends AbstractDialect {
	@Override
	public String getPrefix() {
		return "app";
	}

	@Override
	public boolean isLenient() {
		return false;
	}

	@Override
	public Set<IProcessor> getProcessors() {
		Set<IProcessor> processors = new HashSet<>();
		processors.add(new AbstractAttrProcessor("navlink") {
			@Override
			protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
				String value = element.getAttributeValue(attributeName);
				String navSection = (String) arguments.getContext().getVariables().get("navSection");
				if (value.equals(navSection)) {
					element.setAttribute("class", "navbar-link active");
				} else {
					element.setAttribute("class", "navbar-link");
				}
				element.removeAttribute(attributeName);
				return ProcessorResult.OK;
			}

			@Override
			public int getPrecedence() {
				return 10000;
			}
		});

		processors.add(new AbstractAttrProcessor("blog-category-link") {
			@Override
			protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
				String value = StandardExpressionProcessor.processExpression(
						arguments, element.getAttributeValue(attributeName)).toString();

				String navSection = (String) arguments.getContext().getVariables().get("activeCategory");
				if (value.equals(navSection)) {
					element.setAttribute("class", "blog-category active");
				} else {
					element.setAttribute("class", "blog-category");
				}
				element.removeAttribute(attributeName);
				return ProcessorResult.OK;
			}

			@Override
			public int getPrecedence() {
				return 10000;
			}
		});

		return processors;
	}
}
