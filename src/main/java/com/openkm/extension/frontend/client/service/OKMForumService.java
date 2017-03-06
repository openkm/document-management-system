/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.extension.frontend.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;

import java.util.List;

/**
 * OKMForumService
 *
 * @author jllort
 *
 */
@RemoteServiceRelativePath("../extension/Forum")
public interface OKMForumService extends RemoteService {
	public List<GWTForumTopic> getTopicsByForum(long id) throws OKMException;

	public List<GWTForumTopic> getTopicsByNode(String uuid) throws OKMException;

	public GWTForumTopic createTopic(long id, String uuid, GWTForumTopic topic) throws OKMException;

	public GWTForumTopic findTopicByPK(long id) throws OKMException;

	public void createPost(long forumId, long topicId, GWTForumPost post) throws OKMException;

	public void increaseTopicView(long id) throws OKMException;

	public Boolean deletePost(long forumId, long topicId, long postId) throws OKMException;

	public void updatePost(GWTForumPost post) throws OKMException;

	public List<GWTForum> getAllForum() throws OKMException;

	public GWTForum createForum(GWTForum forum) throws OKMException;

	public void deleteForum(long id) throws OKMException;

	public void updateForum(GWTForum forum) throws OKMException;

	public void updateTopic(long id, GWTForumPost post) throws OKMException;
}