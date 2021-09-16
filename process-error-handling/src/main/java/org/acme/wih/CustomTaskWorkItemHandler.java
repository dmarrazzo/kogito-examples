/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.wih;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

public class CustomTaskWorkItemHandler implements KogitoWorkItemHandler {

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        System.out.println("Hello from the custom work item definition.");
        System.out.println("Passed parameters:");

        // Printing task’s parameters, it will also print
        // our value we pass to the task from the process
        for (String parameter : workItem.getParameters().keySet()) {
            System.out.println(parameter + " = " + workItem.getParameters().get(parameter));
        }

        String input = (String) workItem.getParameter("Input");

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "Hello " + input);

        if (input.contains("error")) {
            Throwable throwable = new Throwable("Input contains \"error\"");
            throw new ProcessWorkItemHandlerException("error_handling", ProcessWorkItemHandlerException.HandlingStrategy.RETHROW, throwable);
        }

        // Don’t forget to finish the work item otherwise the process
        // will be active infinitely and never will pass the flow
        // to the next node.
        manager.completeWorkItem(workItem.getStringId(), results);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        System.err.println("Error happened in the custom work item definition.");
    }
}