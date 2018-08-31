package com.unistacks.tamboo.kafkaclient.pojo;


import com.unistack.tamboo.commons.utils.TambooConfig;

import java.util.Properties;


public class Credential {
	private String publicCredential;
	private String privateCredential;

	public Credential() {
	}

	public Credential(Properties props) {
		this.publicCredential = props.getProperty(TambooConfig.PUBLIC_CREDENTIAL_CONFIG);
		this.privateCredential = props.getProperty(TambooConfig.PRIVATE_CREDENTIAL_CONFIG);
	}

	public String getPublicCredential() {
		return publicCredential;
	}

	public void setPublicCredential(String publicCredential) {
		this.publicCredential = publicCredential;
	}

	public String getPrivateCredential() {
		return privateCredential;
	}

	public void setPrivateCredential(String privateCredential) {
		this.privateCredential = privateCredential;
	}

	public boolean isSet() {
		return publicCredential != null && privateCredential != null;
	}
}
