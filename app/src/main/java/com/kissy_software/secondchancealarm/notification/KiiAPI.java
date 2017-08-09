package com.kissy_software.secondchancealarm.notification;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.PushBackend;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.Site;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
import org.json.JSONObject;

import java.util.List;

public class KiiAPI {
    public static final String APP_ID = "<<your app id>>";
    public static final String APP_KEY = "<<your app key>>";
    public static final Kii.Site APP_SITE_CLOUD = Kii.Site.JP;
    public static final Site APP_SITE_THING = Site.JP;

    public static final String THING_TYPE = "SecondChanceAlarm";
    public static final String SCHEMA_NAME = "SecondChanceAlarmSchema";
    public static final int SCHEMA_VERSION = 1;

    private AndroidDeferredManager mAdm;
    private ThingIFAPI mApi;

    public KiiAPI(AndroidDeferredManager adm, ThingIFAPI api) {
        mAdm = adm;
        mApi = api;
    }

    public Promise<KiiUser, Throwable, Void> addUser(final String username, final String password) {
        return mAdm.when(new DeferredAsyncTask<Void, Void, KiiUser>() {
            @Override
            protected KiiUser doInBackgroundSafe(Void... voids) throws Exception {
                KiiUser.Builder builder = KiiUser.builderWithName(username);
                KiiUser user = builder.build();
                user.register(password);
                return user;
            }
        });
    }

    public Promise<ThingIFAPI, Throwable, Void> initializeThingIFAPI(final Context context, final String username, final String userPassword, final String vendorThingID, final String thingPassword) {
        return mAdm.when(new DeferredAsyncTask<Void, Void, ThingIFAPI>() {
            @Override
            protected ThingIFAPI doInBackgroundSafe(Void... voids) throws Exception {
                KiiUser ownerUser;
                try {
                    ownerUser = KiiUser.logIn(username, userPassword);
                } catch (AppException e) {
                    if (!"invalid_grant".equals(e.getErrorCode())) {
                        throw e;
                    }
                    KiiUser.Builder builder = KiiUser.builderWithName(username);
                    ownerUser = builder.build();
                    ownerUser.register(userPassword);
                }
                String userID = ownerUser.getID();
                String accessToken = ownerUser.getAccessToken();

                TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
                Owner owner = new Owner(typedUserID, accessToken);

                SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(THING_TYPE, SCHEMA_NAME, SCHEMA_VERSION, DummyState.class);
                sb.addActionClass(CheckSensorAction.class, CheckSensorActionResult.class);
                Schema schema = sb.build();

                KiiApp app = new KiiApp(APP_ID, APP_KEY, APP_SITE_THING);
                ThingIFAPIBuilder ib = ThingIFAPIBuilder.newBuilder(context.getApplicationContext(), app, owner);
                ib.addSchema(schema);
                mApi = ib.build();

                JSONObject properties = new JSONObject();
                mApi.onboard(vendorThingID, thingPassword, THING_TYPE, properties);

                return mApi;
            }
        });
    }

    public Promise<Command, Throwable, Void> postNewCommand(final List<Action> actions) {
        return mAdm.when(new DeferredAsyncTask<Void, Void, Command>() {
            @Override
            protected Command doInBackgroundSafe(Void... voids) throws Exception {
                return mApi.postNewCommand(SCHEMA_NAME, SCHEMA_VERSION, actions);
            }
        });
    }

    public Promise<Command, Throwable, Void> getCommand(final String commandID) {
        return mAdm.when(new DeferredAsyncTask<Void, Void, Command>() {
            @Override
            protected Command doInBackgroundSafe(Void... voids) throws Exception {
                return mApi.getCommand(commandID);
            }
        });
    }

    public Promise<Void, Throwable, Void> registerFCMToken() {
        return mAdm.when(new DeferredAsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackgroundSafe(Void... voids) throws Exception {
                String fcmToken = FirebaseInstanceId.getInstance().getToken();
                if (fcmToken == null) {
                    throw new IllegalStateException("FCM device token is not ready.");
                }
                mApi.installPush(fcmToken, PushBackend.GCM);
                return null;
            }
        });
    }
}
