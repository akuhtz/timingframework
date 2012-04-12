package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.junit.Assert;
import org.junit.Test;

public final class TestPropertySetter {

  @SuppressWarnings("unused")
  private static class PropTest {

    private int value;

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

    private byte byteValue;

    public byte sneakyGetByteValue() {
      return byteValue;
    }

    public void setByteValue(byte byteValue) {
      this.byteValue = byteValue;
    }

    @Override
    public String toString() {
      return "PropTest [value=" + value + ", byteValue=" + byteValue + "]";
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSuchProperty1() {
    PropTest pt = new PropTest();
    PropertySetter.getTarget(pt, "wrong", 1, 2, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSuchPropertyTo1() {
    PropTest pt = new PropTest();
    PropertySetter.getTargetTo(pt, "wrong", 1, 2, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSuchPropertyTo2() {
    PropTest pt = new PropTest();
    PropertySetter.getTargetTo(pt, "byteValue", (byte) 1, (byte) 2, (byte) 3);
  }

  @Test
  public void valueProperty() {
    PropTest pt = new PropTest();
    TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 2, 3);
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).addTarget(tt).build();
    a.start();
    ts.tick();
    Assert.assertEquals(1, pt.getValue());
    while (a.isRunning())
      ts.tick();
    Assert.assertEquals(3, pt.getValue());
  }

  @Test
  public void byteValueProperty() {
    PropTest pt = new PropTest();
    TimingTarget tt = PropertySetter.getTarget(pt, "byteValue", (byte) 1, (byte) 2, (byte) 3);
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).addTarget(tt).build();
    a.start();
    ts.tick();
    Assert.assertEquals((byte) 1, pt.sneakyGetByteValue());
    while (a.isRunning())
      ts.tick();
    Assert.assertEquals((byte) 3, pt.sneakyGetByteValue());
  }

  @Test
  public void valuePropertyTo() {
    PropTest pt = new PropTest();
    pt.setValue(100);
    TimingTarget tt = PropertySetter.getTargetTo(pt, "value", 1, 2, 3);
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).addTarget(tt).build();
    a.start();
    ts.tick();
    Assert.assertEquals(100, pt.getValue());
    while (a.isRunning())
      ts.tick();
    Assert.assertEquals(3, pt.getValue());
  }

  @Test
  public void valueSetInBegin() {
    class Expected extends TimingTargetAdapter {

      private PropTest f_object;
      private int f_value;

      Expected(PropTest object) {
        f_object = object;
      }

      public int getValueAtBegin() {
        return f_value;
      }

      @Override
      public void begin(Animator source) {
        f_value = f_object.getValue();
      }
    }
    PropTest pt = new PropTest();
    TimingTarget tt = PropertySetter.getTarget(pt, "value", 1, 2);
    Expected ee = new Expected(pt);
    ManualTimingSource ts = new ManualTimingSource();
    Animator a = new Animator.Builder(ts).addTarget(tt).addTarget(ee).build();
    pt.setValue(-999);
    a.start();
    ts.tick();
    Assert.assertEquals(1, ee.getValueAtBegin());
    while (a.isRunning())
      ts.tick();
    Assert.assertEquals(2, pt.getValue());
  }
}
