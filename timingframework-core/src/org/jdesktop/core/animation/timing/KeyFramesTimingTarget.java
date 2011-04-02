package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.i18n.I18N;

public abstract class KeyFramesTimingTarget<T> extends TimingTargetAdapter {

  private final KeyFrames<T> f_keyFrames;

  public KeyFramesTimingTarget(KeyFrames<T> keyFrames) {
    if (keyFrames == null)
      throw new IllegalArgumentException(I18N.err(1, "keyFrames"));
    f_keyFrames = keyFrames;
  }

  public KeyFrames<T> getKeyFrames() {
    return f_keyFrames;
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    final T value = f_keyFrames.getEvaluatedValueAt(fraction);
    valueChangeTimingEvent(value, fraction, source);
  }

  public abstract void valueChangeTimingEvent(T value, double fraction, Animator source);
}
