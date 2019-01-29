package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.ui.SelectionPanelActionController.SingletonHolder;

class SelectionPanelActionController
{

	public static class SingletonHolder
	{

		private final static SelectionPanelActionController INSTANCE = new SelectionPanelActionController();
	}

	public static SelectionPanelActionController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	SelectionPanel actee;
	SelectionPanel actor;
	ModuleSelectionPanelAction action;

	public void setAction(ModuleSelectionPanelAction action)
	{
		this.action = action;
	}

	public void clearAction()
	{
		action = ModuleSelectionPanelAction.NONE;
	}

	public SelectionPanel getActee()
	{
		return actee;
	}

	public void setActee(SelectionPanel actee)
	{
		this.actee = actee;
	}

	public void removeActee(SelectionPanel actee)
	{
		if (this.actee == actee)
		{
			this.actee = null;
		}
	}

	public SelectionPanel getActor()
	{
		return actor;
	}

	public void setActor(SelectionPanel actor, boolean modifiedAction)
	{
		this.actor = actor;

		// if we have an actor
		if (actor != null)
		{
			if (modifiedAction && (actor.getShipX() != -1 && actor.getShipY() != -1))
			{
				// alt was clicked also, and we're in the normal ship area....
				action = ModuleSelectionPanelAction.PREADD;
			}
			else if (actor.getShipX() == -1 && actor.getShipY() == -1)
			{
				// alt wasn't clicked, we aren't in the normal ship area (i.e. coming from the side bar)
				action = ModuleSelectionPanelAction.PREADD;
			}
		}
	}

	public ModuleSelectionPanelAction getAction()
	{
		if (actee == null || (actee.getShipX() == -1 && actee.getShipY() == -1))
		{
			action = ModuleSelectionPanelAction.NONE;
		}
		else if (actor == actee)
		{
			if (action == ModuleSelectionPanelAction.PREDELETE)
			{
				action = ModuleSelectionPanelAction.DELETE;
			}
			else
			{
				action = ModuleSelectionPanelAction.ROTATE;
			}
		}
		else if (action == ModuleSelectionPanelAction.PREADD)
		{
			action = ModuleSelectionPanelAction.ADD;
		}
		else
		{
			action = ModuleSelectionPanelAction.SWAP;
		}
		return action;
	}
}