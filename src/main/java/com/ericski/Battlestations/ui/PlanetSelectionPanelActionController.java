package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.ui.PlanetSelectionPanelActionController.SingletonHolder;

class PlanetSelectionPanelActionController
{

	public static class SingletonHolder
	{

		private final static PlanetSelectionPanelActionController INSTANCE = new PlanetSelectionPanelActionController();
	}

	public static PlanetSelectionPanelActionController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	PlanetSelectionPanel actee;
	PlanetSelectionPanel actor;
	ModuleSelectionPanelAction action;

	public void setAction(ModuleSelectionPanelAction action)
	{
		this.action = action;
	}

	public void clearAction()
	{
		action = ModuleSelectionPanelAction.NONE;
	}

	public PlanetSelectionPanel getActee()
	{
		return actee;
	}

	public void setActee(PlanetSelectionPanel actee)
	{
		this.actee = actee;
	}

	public void removeActee(PlanetSelectionPanel actee)
	{
		if (this.actee == actee)
		{
			this.actee = null;
		}
	}

	public PlanetSelectionPanel getActor()
	{
		return actor;
	}

	public void setActor(PlanetSelectionPanel actor, boolean modifiedAction)
	{
		this.actor = actor;

		// if we have an actor
		if (actor != null)
		{
			if (modifiedAction && (actor.getGridX() != -1 && actor.getGridY() != -1))
			{
				// alt was clicked also, and we're in the normal ship area....
				action = ModuleSelectionPanelAction.PREADD;
			}
			else if (actor.getGridX() == -1 && actor.getGridY() == -1)
			{
				// alt wasn't clicked, we aren't in the normal ship area (i.e. coming from the side bar)
				action = ModuleSelectionPanelAction.PREADD;
			}
		}
	}

	public ModuleSelectionPanelAction getAction()
	{
		if (actee == null || (actee.getGridX() == -1 && actee.getGridY() == -1))
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