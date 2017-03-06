/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.workflow;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.identity.Group;
import org.jbpm.identity.User;
import org.jbpm.identity.assignment.ExpressionAssignmentException;
import org.jbpm.identity.assignment.ExpressionSession;
import org.jbpm.identity.assignment.TermTokenizer;
import org.jbpm.taskmgmt.exe.Assignable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExpressionAssignmentHandler extends org.jbpm.identity.assignment.ExpressionAssignmentHandler {
	private static final long serialVersionUID = 1L;

	@Override
	protected ExpressionSession getExpressionSession() {
		return new IdentitySession();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void assign(Assignable assignable, ExecutionContext executionContext) {
		try {
			expressionSession = getExpressionSession();

			if (expressionSession == null) {
				throw new NullPointerException("getIdentitySession returned null");
			}

			this.tokenizer = new TermTokenizer(expression);
			this.executionContext = executionContext;
			entity = resolveFirstTerm(tokenizer.nextTerm());

			while (tokenizer.hasMoreTerms() && (entity != null)) {
				entity = resolveNextTerm(tokenizer.nextTerm());
			}

			// if the expression did not resolve to an actor
			if (entity == null) {
				// throw an exception
				throw new RuntimeException("couldn't resolve assignment expression '" + expression + "'");

				// else if the expression evaluated to a user
			} else if (entity instanceof User) {
				// do direct assignment
				assignable.setActorId(entity.getName());

				// else if the expression evaluated to a group
			} else if (entity instanceof Group) {
				// put the group in the pool
				Group group = (Group) entity;
				List<String> pooledActors = new ArrayList<String>();

				for (User user : (Set<User>) group.getUsers()) {
					pooledActors.add(user.getName());
				}

				assignable.setPooledActors(pooledActors.toArray(new String[pooledActors.size()]));
			}
		} catch (RuntimeException e) {
			throw new ExpressionAssignmentException("couldn't resolve assignment expression '" + expression + "'", e);
		}
	}
}
