package se.magnus.util.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtil {

	private final String port;

	private String serviceAddress = null;

	public ServiceUtil(@Value("${server.port}") String port) {
		this.port = port;
	}

	public String getServiceAddress() {
		if (serviceAddress == null) {
			serviceAddress = findMyHostname() + "/" + findMyIpAddress() + ":" + port;
		}
		return serviceAddress;
	}

	private String findMyHostname() {
		try {
			return InetAddress.getLocalHost().getHostName(); // On local: LAPTOP-UP3N2660
		} catch (UnknownHostException e) {
			return "unknown host name";
		}
	}

	private String findMyIpAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress(); // On local: 172.27.96.1:7002
		} catch (UnknownHostException e) {
			return "unknown IP address";
		}
	}

}
