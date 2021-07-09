package org.machinelearning.swissknife.ui.components.utils;

@FunctionalInterface
public interface TriFunction<F, S, T> {

  void apply(F first, S second, T third);
}
