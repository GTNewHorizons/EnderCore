package com.enderio.core.client.render;

import java.util.Collection;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

public class VertexTransformComposite implements VertexTransform {

    public final VertexTransform[] xforms;

    public VertexTransformComposite(VertexTransform... xforms) {
        this.xforms = xforms;
    }

    VertexTransformComposite(Collection<VertexTransform> xformsIn) {
        xforms = new VertexTransform[xformsIn.size()];
        int i = 0;
        for (VertexTransform xform : xformsIn) {
            xforms[i] = xform;
            i++;
        }
    }

    @Override
    public void apply(Vertex vertex) {
        for (int i = 0; i < xforms.length; i++) {
            xforms[i].apply(vertex);
        }
    }

    @Override
    public void apply(Vector3d vec) {
        for (int i = 0; i < xforms.length; i++) {
            xforms[i].apply(vec);
        }
    }

    @Override
    public void applyToNormal(Vector3f vec) {
        for (int i = 0; i < xforms.length; i++) {
            xforms[i].applyToNormal(vec);
        }
    }

}
