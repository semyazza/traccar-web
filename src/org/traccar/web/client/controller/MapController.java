/*
 * Copyright 2013 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.web.client.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.traccar.web.client.Application;
import org.traccar.web.client.view.MapView;
import org.traccar.web.shared.model.Device;
import org.traccar.web.shared.model.Position;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class MapController implements ContentController {

    private static final int UPDATE_INTERVAL = 15000;

    private MapView mapView;

    public MapController() {
        mapView = new MapView();
    }

    @Override
    public ContentPanel getView() {
        return mapView.getView();
    }

    private Timer updateTimer;

    @Override
    public void run() {
        updateTimer = new Timer() {
            @Override
            public void run() {
                update();
            }
        };
        update();
    }

    public void update() {
        updateTimer.cancel();
        Application.getDataService().getLatestPositions(new AsyncCallback<List<Position>>() {
            @Override
            public void onSuccess(List<Position> result) {
                mapView.showLatestPositions(result);
                updateTimer.schedule(UPDATE_INTERVAL);
            }
            @Override
            public void onFailure(Throwable caught) {
                updateTimer.schedule(UPDATE_INTERVAL);
            }
        });
    }

    public void selectDevice(Device device) {
        mapView.selectDevice(device);
    }

    public void showArchivePositions(List<Position> positions) {
        List<Position> sortedPositions = new LinkedList<Position>(positions);
        Collections.sort(sortedPositions, new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        mapView.showArchivePositions(sortedPositions);
    }

    public void selectArchivePosition(Position position) {
        mapView.selectArchivePosition(position);
    }

}
