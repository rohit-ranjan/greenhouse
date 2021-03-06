/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.greenhouse.develop;

import java.util.List;


/**
 * Data access interface for managing client Applications and the member account connections established with them.
 * @author Keith Donald
 */
public interface AppRepository {

	// for supporting a developer's registered Apps
	
	/**
	 * Get a short summary of all the applications the member is a developer for.
	 * Used to present the list of registered applications to their developer.
	 * @param accountId the member account id
	 */
	List<AppSummary> findAppSummaries(Long accountId);

	/**
	 * Get a detailed view of a single Application the member is a developer for.
	 * Used to present the details of a registered application to its developer.
	 * @param accountId the member account id
	 * @param slug a short, meaningful key that identifies the application
	 */
	App findAppBySlug(Long accountId, String slug);

	/**
	 * Update the details of a registered Application the member is a developer for.
	 * Used by the developer to change aspects of their registered application.
	 * @param accountId the member account id
	 * @param slug a short, meaningful key that identifies the application
	 * @param form the form containing the updates
	 * @return the new app slug, which may have changed if the app's name has also changed.
	 */
	String updateApp(Long accountId, String slug, AppForm form);

	/**
	 * Delete an application the member is a developer for.
	 * Used by the developer when they no longer wish to connect their application to this system.
	 * @param accountId the member account id
	 * @param slug a short, meaningful key that identifies the application
	 */
	void deleteApp(Long accountId, String slug);

	/**
	 * Get a blank AppForm for registering a new Application.
	 * Allows this AppRepository to control default form field values.
	 */
	AppForm getNewAppForm();

	/**
	 * Retrieve the AppForm for editing an existing Application.
	 * The form's fields will be pre-filled with existing values.
	 * @param accountId the member account id
	 * @param slug a short, meaningful key that identifies the application
	 */
	AppForm getAppForm(Long accountId, String slug);

	/**
	 * Register a new Application using the form submitted by the member.
	 * The app's apiKey and secret will be assigned internally. 
	 * The submitting member will become the first developer for the app.
	 * @param accountId the member account id
	 * @param form the app form
	 * @return a short, meaningful key that identifies the application; designed to be used to redirect the user to the app details view.
	 */
	String createApp(Long accountId, AppForm form);
	
	// for supporting AppConnections
	
	/**
	 * Get a detailed description of the Application assigned the apiKey.
	 * Used to present details of the application to a member during connection authorization.
	 * @param apiKey the assigned api key, submitted by the client application itself on behalf of the member
	 * @throws InvalidApiKeyException the key provided by the client is not valid
	 */
	App findAppByApiKey(String apiKey) throws InvalidApiKeyException;
	
	/**
	 * Connect the member to the Application assigned the api key.
	 * Called to grant the application access to the member's account.
	 * Called only after member authorization.
	 * @param accountId the member account id
	 * @param apiKey the api key
	 * @return a model for the new connection, to be used to send the connection details such as the assigned accessToken back to the client
	 * @throws InvalidApiKeyException the app api key provided by the client is not valid
	 */
	AppConnection connectApp(Long accountId, String apiKey) throws InvalidApiKeyException;

	/**
	 * Find the App-to-Member Connection with the client-provided access token.
	 * Called by the client application when making a request for a protected resource.
	 * @param accessToken the access token serving as a key for the connection that was assigned on {@link #connectApp(Long, String)}.
	 * @return a model for the connection, to be used to validate the client request for a protected resource
	 * @throws NoSuchAccountConnectionException no such connection exists for the provided access token; this could happen if the connection was severed due to explicit disconnect or expiration.
	 */
	AppConnection findAppConnection(String accessToken) throws NoSuchAccountConnectionException;
	
	/**
	 * Disconnect the connection between an Application and a Member that assigned the provided access token.
	 * Called by the member or an administrator to revoke access by the application to the member's data.
	 * @param accountId the member account id 
	 * @param accessToken the access token serving as the key for the connection
	 */
	void disconnectApp(Long accountId, String accessToken);
	
}