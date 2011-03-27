package org.jdesktop.core.animation.timing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class TestKeyTimes {

  @Test(expected = IllegalArgumentException.class)
  public void firstTimeMustBeZero1() {
    KeyTimes.build(1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void firstTimeMustBeZero2() {
    KeyTimes.build(0.1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void firstTimeMustBeZero3() {
    KeyTimes.build(-1, 0, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lastTimeMustBeOne1() {
    KeyTimes.build(0, 0.5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lastTimeMustBeOne2() {
    KeyTimes.build(0, 1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lastTimeMustBeOne3() {
    KeyTimes.build(0, 0.5, 1.1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void timesMustBeInIncreasingOrder1() {
    KeyTimes.build(0, 0.5, 0.49, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void timesMustBeInIncreasingOrder2() {
    KeyTimes.build(0, -1, 1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void get1() {
    final KeyTimes k = KeyTimes.build(0, 0.1, 0.2, 0.5, 1);
    k.get(-1);
  }

  @Test
  public void get2() {
    final KeyTimes k = KeyTimes.build(0, 0.1, 0.2, 0.5, 1);
    assertEquals(0, k.get(0), 1e-8);
    assertEquals(0.1, k.get(1), 1e-8);
    assertEquals(0.2, k.get(2), 1e-8);
    assertEquals(0.5, k.get(3), 1e-8);
    assertEquals(1, k.get(4), 1e-8);

  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void get3() {
    final KeyTimes k = KeyTimes.build(0, 0.1, 0.2, 0.5, 1);
    k.get(5);
  }

  @Test
  public void getInterval1() {
    final KeyTimes k = KeyTimes.build(0, 0.1, 0.2, 0.5, 1);
    assertEquals(0, k.getInterval(-1));
    assertEquals(0, k.getInterval(0));
    assertEquals(0, k.getInterval(0.1));
    assertEquals(1, k.getInterval(0.11));
    assertEquals(1, k.getInterval(0.15));
    assertEquals(1, k.getInterval(0.2));
    assertEquals(2, k.getInterval(0.21));
    assertEquals(2, k.getInterval(0.3));
    assertEquals(2, k.getInterval(0.499));
    assertEquals(2, k.getInterval(0.5));
    assertEquals(3, k.getInterval(0.51));
    assertEquals(3, k.getInterval(0.6));
    assertEquals(3, k.getInterval(0.99999));
    assertEquals(3, k.getInterval(1));
    assertEquals(3, k.getInterval(2));
  }

  @Test
  public void getInterval2() {
    final KeyTimes k = KeyTimes.build(0, 1);
    assertEquals(0, k.getInterval(-1));
    assertEquals(0, k.getInterval(0));
    assertEquals(0, k.getInterval(0.5));
    assertEquals(0, k.getInterval(1));
    assertEquals(0, k.getInterval(2));
  }
}
