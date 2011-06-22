/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.map.api;

import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPServiceSupplementary;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface MAPProvider {

	public static final int NETWORK_UNSTRUCTURED_SS_CONTEXT_V2 = 1;

	/**
	 * Add MAP Dialog listener to the Stack
	 * 
	 * @param mapDialogListener
	 */
	public void addMAPDialogListener(MAPDialogListener mapDialogListener);

	/**
	 * Remove MAP DIalog Listener from the stack
	 * 
	 * @param mapDialogListener
	 */
	public void removeMAPDialogListener(MAPDialogListener mapDialogListener);

	// /**
	// * Add MAP Service listener to the stack
	// *
	// * @param mapServiceListener
	// */
	// public void addMAPServiceListener(MAPServiceListener mapServiceListener);
	//
	// /**
	// * Remove MAP Service listener from the stack
	// *
	// * @param mapServiceListener
	// */
	// public void removeMAPServiceListener(MAPServiceListener
	// mapServiceListener);

	/**
	 * Get the {@link MapServiceFactory}
	 * 
	 * @return
	 */
	public MapServiceFactory getMapServiceFactory();

	/**
	 * Get {@link MAPDialog} corresponding to passed dialogId
	 * 
	 * @param dialogId
	 * @return
	 */
	public MAPDialog getMAPDialog(Long dialogId);

	public MAPServiceSupplementary getMAPServiceSupplementary();

}
