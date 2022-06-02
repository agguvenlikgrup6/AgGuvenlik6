package org.uludag.bmb.controller.scene;

import java.util.List;

import org.uludag.bmb.beans.database.Notification;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.scene.control.Hyperlink;

public class NotificationPaneController {
    public NotificationPaneController(MainSceneController msc) {
        start(msc);
    }

    private void start(MainSceneController msc){
        Timeline notificationCycle = new Timeline(
            new KeyFrame(Duration.seconds(2),
                    new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            List<Notification> notifications = msc.notificationOperations.getAll();
                            if (notifications.size() != 0 && notifications != null) {
                                try {
                                    String path = "/";
                                    for (int i = 1; i < msc.selectedDirectoryPathPane.getItems().size(); i++) {
                                        path += ((Hyperlink) msc.selectedDirectoryPathPane.getItems().get(i)).getText();
                                    }
                                    var items = msc.fileRecordOperations.getByPath(path);
                                    msc.fileListView.setItems(items);
                                    msc.fileListView.refresh();
                                } catch (IndexOutOfBoundsException e) {

                                }
                                for (Notification notification : notifications) {
                                    msc.notificationListView.getItems().add(0, notification.getMessage());
                                    msc.notificationDot.visibleProperty().set(true);
                                    ScaleTransition st = new ScaleTransition(Duration.millis(1000),
                                            msc.notificationDot);
                                    st.setFromX(0.6);
                                    st.setFromY(0.6);
                                    st.setToX(1.2);
                                    st.setToY(1.2);
                                    st.setCycleCount(3);
                                    st.setAutoReverse(true);
                                    st.jumpTo(Duration.millis(200));
                                    st.play();
                                }
                                msc.notificationOperations.deleteAll();
                            }
                        }
                    }));
    notificationCycle.setCycleCount(Timeline.INDEFINITE);
    notificationCycle.play();
    }
}
