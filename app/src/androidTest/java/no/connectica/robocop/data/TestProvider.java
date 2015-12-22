package no.connectica.robocop.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;

import no.connectica.robocop.data.provider.AgendaProvider;

/**
 * Created by Chirag on 12/22/15.
 */
public class TestProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                AgendaProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("TestError: AgendaProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
}
