package org.jdesktop.core.animation.timing;

import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;
import org.jdesktop.core.animation.timing.sources.ManualTimingSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TestTimingSource {

  int taskCounter;
  int tickCounter;
  int postTickCounter;

  @org.junit.jupiter.api.Test
  public void tick1() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    ts.addTickListener(new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    Assertions.assertEquals(0, tickCounter);
  }

  @Test
  public void tick2() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    ts.addTickListener(new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.tick();
    Assertions.assertEquals(1, tickCounter);
  }

  @org.junit.jupiter.api.Test
  public void tick3() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    ts.addTickListener(new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, tickCounter);
  }

  @Test
  public void tick4() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    ts.addTickListener(new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    for (int i = 0; i < 1000; i++)
      ts.tick();
    Assertions.assertEquals(1000, tickCounter);
  }

  @Test
  public void tickRemove() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    final TickListener tl = new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    };
    ts.addTickListener(tl);
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, tickCounter);
    ts.removeTickListener(tl);
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, tickCounter);
  }

  @Test
  public void postTick1() {
    final ManualTimingSource ts = new ManualTimingSource();
    postTickCounter = 0;
    ts.addPostTickListener(new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    Assertions.assertEquals(0, postTickCounter);
  }

  @Test
  public void postTick2() {
    final ManualTimingSource ts = new ManualTimingSource();
    postTickCounter = 0;
    ts.addPostTickListener(new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.tick();
    Assertions.assertEquals(1, postTickCounter);
  }

  @Test
  public void postTick3() {
    final ManualTimingSource ts = new ManualTimingSource();
    postTickCounter = 0;
    ts.addPostTickListener(new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, postTickCounter);
  }

  @Test
  public void postTick4() {
    final ManualTimingSource ts = new ManualTimingSource();
    postTickCounter = 0;
    ts.addPostTickListener(new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    for (int i = 0; i < 1000; i++)
      ts.tick();
    Assertions.assertEquals(1000, postTickCounter);
  }

  @org.junit.jupiter.api.Test
  public void postTickRemove() {
    final ManualTimingSource ts = new ManualTimingSource();
    postTickCounter = 0;
    final PostTickListener tl = new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    };
    ts.addPostTickListener(tl);
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, postTickCounter);
    ts.removePostTickListener(tl);
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, postTickCounter);
  }

  @Test
  public void tickPostTickOrder1() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    postTickCounter = 0;
    ts.addTickListener(new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        Assertions.assertEquals(tickCounter, postTickCounter);
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.addPostTickListener(new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        Assertions.assertEquals(tickCounter - 1, postTickCounter);
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.tick();
    ts.tick();
    Assertions.assertEquals(2, tickCounter);
    Assertions.assertEquals(2, postTickCounter);
  }

  @Test
  public void tickPostTickOrder2() {
    final ManualTimingSource ts = new ManualTimingSource();
    tickCounter = 0;
    postTickCounter = 0;
    ts.addTickListener(new TickListener() {
      public void timingSourceTick(TimingSource source, long nanoTime) {
        Assertions.assertEquals(tickCounter, postTickCounter);
        tickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    ts.addPostTickListener(new PostTickListener() {
      public void timingSourcePostTick(TimingSource source, long nanoTime) {
        Assertions.assertEquals(tickCounter - 1, postTickCounter);
        postTickCounter++;
        Assertions.assertSame(ts, source);
      }
    });
    for (int i = 0; i < 1000; i++)
      ts.tick();
    Assertions.assertEquals(1000, tickCounter);
    Assertions.assertEquals(1000, postTickCounter);
  }

  @Test
  public void runTask() {
    final ManualTimingSource ts = new ManualTimingSource();
    taskCounter = 0;
    ts.submit(new Runnable() {
      public void run() {
        taskCounter++;
      }
    });
    ts.tick();
    Assertions.assertEquals(1, taskCounter);
  }
}
