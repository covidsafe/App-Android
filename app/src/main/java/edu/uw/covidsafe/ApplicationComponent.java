package edu.uw.covidsafe;

import dagger.Component;
import edu.uw.covidsafe.ui.MainActivity;

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity activity);
}
