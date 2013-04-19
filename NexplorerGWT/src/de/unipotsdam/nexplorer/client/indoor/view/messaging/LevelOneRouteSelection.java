package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import de.unipotsdam.nexplorer.client.indoor.NewMessageBinder;

public class LevelOneRouteSelection extends RoutingLevel {

	private static LevelOneRouteSelectionUiBinder uiBinder = GWT.create(LevelOneRouteSelectionUiBinder.class);

	interface LevelOneRouteSelectionUiBinder extends UiBinder<Element, LevelOneRouteSelection> {
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

	public LevelOneRouteSelection() {
		setElement(uiBinder.createAndBindUi(this));
		this.status.appendChild((new NewMessageBinder().getElement()));
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
