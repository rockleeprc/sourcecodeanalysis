/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javafx.scene.control;

/**
Builder class for javafx.scene.control.ToggleGroup
@see javafx.scene.control.ToggleGroup
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class ToggleGroupBuilder<B extends javafx.scene.control.ToggleGroupBuilder<B>> implements javafx.util.Builder<javafx.scene.control.ToggleGroup> {
    protected ToggleGroupBuilder() {
    }

    /** Creates a new instance of ToggleGroupBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.control.ToggleGroupBuilder<?> create() {
        return new javafx.scene.control.ToggleGroupBuilder();
    }

    private boolean __set;
    public void applyTo(javafx.scene.control.ToggleGroup x) {
        if (__set) x.getToggles().addAll(this.toggles);
    }

    private java.util.Collection<? extends javafx.scene.control.Toggle> toggles;
    /**
    Add the given items to the List of items in the {@link javafx.scene.control.ToggleGroup#getToggles() toggles} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B toggles(java.util.Collection<? extends javafx.scene.control.Toggle> x) {
        this.toggles = x;
        __set = true;
        return (B) this;
    }

    /**
    Add the given items to the List of items in the {@link javafx.scene.control.ToggleGroup#getToggles() toggles} property for the instance constructed by this builder.
    */
    public B toggles(javafx.scene.control.Toggle... x) {
        return toggles(java.util.Arrays.asList(x));
    }

    /**
    Make an instance of {@link javafx.scene.control.ToggleGroup} based on the properties set on this builder.
    */
    public javafx.scene.control.ToggleGroup build() {
        javafx.scene.control.ToggleGroup x = new javafx.scene.control.ToggleGroup();
        applyTo(x);
        return x;
    }
}
