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

package com.openkm.extension.frontend.client.widget.forum;

import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;

/**
 * ForumController
 *
 * @author jllort
 *
 */
public interface ForumController {
	public abstract void createNewPost();

	public abstract void createNewTopic(long forumId, String name);

	public abstract void newTopicCreated(long forumId, GWTForumTopic topic);

	public abstract void refreshTopics(long topicId, String name);

	public abstract void refreshTopics(String uuid);

	public abstract void refreshPosts(long topicId, String topicName);

	public abstract void goHome();

	public abstract void deletePost(long postId);

	public abstract void updatePost(GWTForumPost post);

	public abstract void editPost(GWTForumPost post);

	public abstract ForumController getController();

	public abstract void refreshForums();

	public abstract void createForum();

	public abstract void editForum(GWTForum forum);

	public abstract void deleteForum(long forumId);

	public abstract void editTopic(GWTForumPost post);

	public abstract void quotePost(GWTForumPost post);
}