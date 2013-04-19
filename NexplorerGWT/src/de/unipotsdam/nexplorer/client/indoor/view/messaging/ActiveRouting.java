package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

import de.unipotsdam.nexplorer.client.indoor.NewMessageBinder;

public class ActiveRouting extends UIObject {

	private static ActiveRoutingUiBinder uiBinder = GWT.create(ActiveRoutingUiBinder.class);

	interface ActiveRoutingUiBinder extends UiBinder<Element, ActiveRouting> {
	}

	@UiField
	TableElement messageTable;
	@UiField
	SpanElement sourceNode;
	@UiField
	SpanElement destinationNode;
	@UiField
	DivElement currentNodeId;
	@UiField
	DivElement status;
	@UiField
	TableElement messageStatusTable;
	@UiField
	DivElement statusMessage;
	@UiField
	DivElement bonusGoal;

	public ActiveRouting() {
		setElement(uiBinder.createAndBindUi(this));
		NewMessageBinder newMessageBinder = new NewMessageBinder();
		this.status.appendChild(newMessageBinder.getElement());//
	}

	public DivElement getStatus() {
		return this.status;
	}

	public void setSourceNode(String source) {
		this.sourceNode.setInnerText(source);
	}

	public void setDestinationNode(String destination) {
		this.destinationNode.setInnerText(destination);
	}

	public void setCurrentNodeId(String current) {
		this.currentNodeId.setInnerText(current);
	}

	public void setBonusGoal(String bonusGoal) {
		this.bonusGoal.setInnerText(bonusGoal);
	}
}
