/*
 * Copyright © Yolanda. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.support.v7.widget.helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created in 2016/5/7 21:40.
 *
 * @author Yolanda;
 */
public class MosrItemTouchHelper extends ItemTouchHelper {

    /**
     * Creates an ItemTouchHelper that will work with the given Callback.
     * <p/>
     * You can attach ItemTouchHelper to a RecyclerView via
     * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     *
     * @param callback The Callback which controls the behavior of this touch helper.
     */
    public MosrItemTouchHelper(Callback callback) {
        super(callback);
    }

    public Callback getCallback() {
        return mCallback;
    }
    public float getInitialTouchX(){
        return mInitialTouchX;
    }
    public float getInitialTouchY(){
        return mInitialTouchY;
    }
}
