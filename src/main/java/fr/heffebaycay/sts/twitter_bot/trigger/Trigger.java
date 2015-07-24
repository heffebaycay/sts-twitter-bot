package fr.heffebaycay.sts.twitter_bot.trigger;

import fr.heffebaycay.sts.twitter_bot.action.Action;
import fr.heffebaycay.sts.twitter_bot.action.ActionParam;

public abstract class Trigger {

    protected Action action;

    public abstract boolean isTriggered();

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public abstract ActionParam getActionParam();
}
