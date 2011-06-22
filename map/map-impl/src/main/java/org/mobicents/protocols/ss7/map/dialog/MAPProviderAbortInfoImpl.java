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

package org.mobicents.protocols.ss7.map.dialog;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.dialog.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.dialog.MAPProviderAbortInfo;
import org.mobicents.protocols.ss7.map.api.dialog.MAPProviderAbortReason;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MAPProviderAbortInfoImpl implements MAPProviderAbortInfo {

	public static final int MAP_PROVIDER_ABORT_INFO_TAG = 0x05;

	protected static final int PROVIDER_ABORT_TAG_CLASS = Tag.CLASS_CONTEXT_SPECIFIC;
	protected static final boolean PROVIDER_ABORT_TAG_PC_CONSTRUCTED = false;

	private MAPDialog mapDialog = null;
	private MAPProviderAbortReason mapProviderAbortReason = null;
	private MAPExtensionContainer extensionContainer;

	public MAPProviderAbortInfoImpl() {
	}

	public MAPDialog getMAPDialog() {
		return this.mapDialog;
	}

	public MAPProviderAbortReason getMAPProviderAbortReason() {
		return this.mapProviderAbortReason;
	}

	public MAPExtensionContainer getExtensionContainer() {
		return extensionContainer;
	}

	public void setMAPDialog(MAPDialog mapDialog) {
		this.mapDialog = mapDialog;
	}

	public void setMAPProviderAbortReason(MAPProviderAbortReason mapProvAbrtReas) {
		this.mapProviderAbortReason = mapProvAbrtReas;
	}

	public void setExtensionContainer(MAPExtensionContainer extensionContainer) {
		this.extensionContainer = extensionContainer;
	}

	public void decode(AsnInputStream ais) throws AsnException, IOException, MAPException {
		// MAP-ProviderAbortInfo ::= SEQUENCE {
		// map-ProviderAbortReason ENUMERATED {
		// abnormalDialogue ( 0 ),
		// invalidPDU ( 1 ) },
		// ... ,
		// extensionContainer SEQUENCE {
		// privateExtensionList [0] IMPLICIT SEQUENCE ( SIZE( 1 .. 10 ) ) OF
		// SEQUENCE {
		// extId MAP-EXTENSION .&extensionId ( {
		// ,
		// ...} ) ,
		// extType MAP-EXTENSION .&ExtensionType ( {
		// ,
		// ...} { @extId } ) OPTIONAL} OPTIONAL,
		// pcs-Extensions [1] IMPLICIT SEQUENCE {
		// ... } OPTIONAL,
		// ... } OPTIONAL}

		byte[] seqData = ais.readSequence();

		AsnInputStream localAis = new AsnInputStream(new ByteArrayInputStream(seqData));

		int tag;

		Boolean providerAbortReasonHasRead = false;
		int seqz = 0;
		while (localAis.available() > 0) {
			tag = localAis.readTag();
			if (seqz == 0) {
				// first element must be map-ProviderAbortReason
				if (tag != Tag.ENUMERATED)
					throw new MAPException(
							"The first element of MAP-ProviderAbortInfo must be map-ProviderAbortReason when decoding MAP-ProviderAbortInfo");
				int length = localAis.readLength();
				if (length != 1) {
					throw new MAPException(
							"Expected length of MAPProviderAbortInfoImpl.MAPProviderAbortReason to be 1 but found "
									+ length + "  when decoding MAP-ProviderAbortInfo");
				}

				int code = localAis.read();
				this.mapProviderAbortReason = MAPProviderAbortReason.getInstance(code);
				if (this.mapProviderAbortReason == null)
					throw new MAPException(
							"Bad map-ProviderAbortReason code received when decoding MAP-ProviderAbortInfo" + code);
				providerAbortReasonHasRead = true;
			} else {
				if (tag == Tag.SEQUENCE) {
					this.extensionContainer = new MAPExtensionContainerImpl();
					((MAPExtensionContainerImpl) this.extensionContainer).decode(localAis);
				}
			}

			seqz++;
		}
		if (!providerAbortReasonHasRead)
			throw new MAPException(
					"The first element of MAP-ProviderAbortInfo must be map-ProviderAbortReason when decoding MAP-ProviderAbortInfo");
	}

	public void encode(AsnOutputStream asnOS) throws IOException, MAPException {
		if (this.mapProviderAbortReason == null)
			throw new MAPException("MapProviderAbortReason parameter has not set - when encoding MAP-ProviderAbortInfo");

		AsnOutputStream localAos = new AsnOutputStream();

		byte[] data = new byte[3];
		data[0] = Tag.ENUMERATED;
		data[1] = 0x01;
		data[2] = (byte) this.mapProviderAbortReason.getCode();
		int wholeLen = 3;

		byte[] extContData = null;
		if (this.extensionContainer != null) {
			localAos.reset();
			((MAPExtensionContainerImpl) this.extensionContainer).encode(localAos);
			extContData = localAos.toByteArray();
			wholeLen += extContData.length;
		}

		// Now let us write the MAP OPEN-INFO Tags
		asnOS.writeTag(PROVIDER_ABORT_TAG_CLASS, PROVIDER_ABORT_TAG_PC_CONSTRUCTED, MAP_PROVIDER_ABORT_INFO_TAG);
		asnOS.writeLength(wholeLen);
		asnOS.write(data);
		if (extContData != null) {
			localAos.writeTag(Tag.CLASS_UNIVERSAL, PROVIDER_ABORT_TAG_PC_CONSTRUCTED, Tag.SEQUENCE);
			localAos.writeLength(extContData.length);
			localAos.write(extContData);
		}
	}

}
