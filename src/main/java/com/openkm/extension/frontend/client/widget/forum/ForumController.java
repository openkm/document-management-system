/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
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
	void createNewPost();

	void createNewTopic(long forumId, String name);

	void newTopicCreated(long forumId, GWTForumTopic topic);

	void refreshTopics(long topicId, String name);

	void refreshTopics(String uuid);

	void refreshPosts(long topicId, String topicName);

	void goHome();

	void deletePost(long postId);

	void updatePost(GWTForumPost post);

	void editPost(GWTForumPost post);

	ForumController getController();

	void refreshForums();

	void createForum();

	void editForum(GWTForum forum);

	void deleteForum(long forumId);

	void editTopic(GWTForumPost post);

	void quotePost(GWTForumPost post);
}
