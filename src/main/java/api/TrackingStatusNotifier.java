package api;

import com.intellij.util.messages.Topic;

public interface TrackingStatusNotifier {
    Topic<TrackingStatusNotifier> TOPIC =
            Topic.create("CodeGRITS Tracking Status", TrackingStatusNotifier.class);

    enum Status {
        STARTED,
        PAUSED,
        RESUMED,
        STOPPED
    }

    void onStatusChanged(Status status);
}
