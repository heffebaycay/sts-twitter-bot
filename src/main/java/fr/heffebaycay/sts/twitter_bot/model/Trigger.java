package fr.heffebaycay.sts.twitter_bot.model;

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
