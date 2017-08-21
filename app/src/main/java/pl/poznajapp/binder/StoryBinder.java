package pl.poznajapp.binder;

import net.droidlabs.mvvm.recyclerview.adapter.binder.ConditionalDataBinder;

import pl.poznajapp.viewmodel.StoryViewModel;

/**
 * Created by Rafał Gawlik on 18.08.17.
 */

public class StoryBinder extends ConditionalDataBinder<StoryViewModel> {

    public StoryBinder(int bindingVariable, int layoutId) {
        super(bindingVariable, layoutId);
    }

    @Override
    public boolean canHandle(StoryViewModel model) {
        return true;
    }
}

