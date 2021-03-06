package org.stagemonitor.web.monitor;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.web.monitor.filter.StatusExposingByteCountingServletResponse;

public class DefaultMonitoredHttpRequestFactory implements MonitoredHttpRequestFactory {

	public DefaultMonitoredHttpRequestFactory() {
	}

	@Override
	public MonitoredHttpRequest createMonitoredHttpRequest(HttpServletRequest httpServletRequest,
														   StatusExposingByteCountingServletResponse responseWrapper,
														   FilterChain filterChain, Configuration configuration) {
		return new MonitoredHttpRequest(httpServletRequest, responseWrapper, filterChain, configuration);
	}
}
