//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl

//****************************************************************************

import scala.collection.Searching._
import scala.collection.mutable.ArrayBuffer

import com.wolery.owl.core._
import com.wolery.owl.midi.messages._
import com.wolery.owl.core.utilities._
import com.wolery.owl.utils.Logging

import javax.sound.midi.MetaMessage
import javax.sound.midi.Sequencer

//****************************************************************************

final class Transport(m_seq: Sequencer) extends Logging
{
  case class Time(ms: ℕ)
  {
    def hour        : ℕ = (ms / 360000)
    def minute      : ℕ = (ms % 360000) / 60000
    def second      : ℕ = (ms % 60000)  / 60
    def millisecond : ℕ = (ms % 1000)

    override
    def toString: String =
    {
      f"$hour%04d:$minute%02d:$second%02d:$millisecond%02d"
    }
  }

  case class Measure(measure: ℕ = 0,beat: ℕ = 0,subbeat: ℕ = 0)
  {
    override
    def toString: String = f"$measure%04d.$beat%02d.$subbeat%02d"
  }

  case class Region(from: Tick,to: Tick)
  {
    assert(isBetween(from,0L,to ))
    assert(isBetween(to,from,end))

    override
    def toString: String = s"[$from,$to]"
  }

  def isPlaying                       : Bool   = m_seq.isRunning
  def isLooping                       : Bool   = m_seq.getLoopCount != 0

  def end                             : Tick   = m_seq.getTickLength

  def play()                          : Unit   = m_seq.start()
  def stop()                          : Unit   = m_seq.stop()

  def cursor                          : Tick   = m_seq.getTickPosition
  def cursor_=(tick: Tick)            : Unit   = m_seq.setTickPosition(tick)

  def meter                           : Meter  = meter()
  def meter(tick: Tick = cursor)      : Meter  = m_map.m_meter(tick)

  def scale                           : Scale  = scale()
  def scale(tick: Tick = cursor)      : Scale  = m_map.m_scale(tick)

  def tempo                           : Tempo  =tempo()
  def tempo(tick: Tick = cursor)      : Tempo  = m_map.m_tempo(tick) * m_seq.getTempoFactor
  def tempo_=(tempo: Tempo)           : Unit   = m_seq.setTempoFactor((tempo / m_seq.getTempoInBPM).toFloat)

  def loop                            : Region = {val e = m_seq.getLoopEndPoint;Region(m_seq.getLoopStartPoint,if (e<0)end else e)}
  def loop_=(region: Region)          : Unit   = {m_seq.setLoopStartPoint(region.from);m_seq.setLoopStartPoint(region.to);}

  def looping                         : Bool   = m_seq.getLoopCount != 0
  def looping_=(loop: Bool)           : Unit   =
  {
    if (loop)
    {
      m_seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY)
    }
    else
    {
      m_seq.setLoopCount(0)
    }
  }

  val ticksPerBeat                    : ℕ      = m_seq.getSequence.getResolution

//conversions
  def measure(tick: Tick)             : Measure =
  {
    val measure = m_map.m_measure(tick)
    val meter  = m_map.m_meter(tick)
    println(m_map.m_measure(tick))
      Measure(m_map.m_measure(tick))
  }

  def time(tick: Tick)                : Time    = Time(m_map.m_time (tick))
  def tick(time: Time)                : Tick    = m_map.m_time.invert(time.millisecond)
  def tick(measure: Measure)          : Tick    = m_map.m_measure.invert(measure.measure)
///
  private[this]
  object m_map
  {
    val m_meter   = new TickFunction[Meter]       (Meter(4,4))
    val m_tempo   = new TickFunction[Tempo]       (120.0)
    val m_scale   = new TickFunction[Scale]       (Scale(C,"ionian").get)
    val m_measure = new TickBijection[ℕ]          (1)
    val m_time    = new TickBijection[Millisecond](0)
    val m_track   = m_seq.getSequence.getTracks.apply(0)

    for (i ← 0 until m_track.size)
    {
      val e = m_track.get(i)

      e.getMessage match
      {
        case m: MetaMessage ⇒ m.getType match
        {
          case METER ⇒
          {
            m_measure += (e.getTick,(e,v) ⇒ (v + e / (ticksPerBeat * m_meter.recent.beats)).toInt)
            m_meter   += (e.getTick,m.meter)
          }
          case TEMPO ⇒
          {
            m_time    += (e.getTick,(e,v) ⇒ (v + e / (ticksPerBeat * m_tempo.recent) * 60e3).toInt)
            m_tempo   += (e.getTick,m.tempo)
          }
          case SCALE ⇒
          {
            m_scale   += (e.getTick,m.scale)
          }
          case _     ⇒
        }
        case _       ⇒
      }
    }

    override
    def toString: String =
    {
      s" m_meter  : $m_meter\n"   +
      s" m_tempo  : $m_tempo\n"   +
      s" m_scale  : $m_scale\n"   +
      s" m_measure: $m_measure\n" +
      s" m_time   : $m_time\n"
    }
  }

  log.debug(s"m_map:\n$m_map")
}

//****************************************************************************

class TickFunction[Value](value: Value,hint: ℕ = 64)
{
  val m_keys = new ArrayBuffer[Tick] (hint)
  val m_vals = new ArrayBuffer[Value](hint)

  m_keys.append(0)
  m_vals.append(value)

  def apply(tick: Tick): Value =
  {
    m_keys.search(tick) match
    {
      case Found(i)          ⇒ m_vals(i)
      case InsertionPoint(i) ⇒ m_vals(i - 1)
    }
  }

  def +=(tick: Tick,value: Value) =
  {
    if (value != m_vals.last)
    {
      if (tick > m_keys.last)
      {
        m_keys.append(tick)
        m_vals.append(value)
      }
      else
      {
        assert(tick == m_keys.last)

        m_vals(m_vals.size - 1) = value
      }
    }

    assert(m_keys.size == m_vals.size)
  }

  def recent: Value = m_vals.last

  override
  def toString: String =
  {
    m_keys.zip(m_vals).mkString("",", ","")
  }
}

//****************************************************************************

class TickBijection[Value: Ordering](value: Value,hint: ℕ = 64) extends
      TickFunction [Value]          (value: Value,hint: ℕ)
{
  def invert(value: Value): Tick =
  {
    m_vals.search(value) match
    {
      case Found(i)          ⇒ m_keys(i)
      case InsertionPoint(i) ⇒ m_keys(i - 1)
    }
  }

  def +=(tick: Tick,next: (Tick,Value) ⇒ Value) =
  {
    super.+= (tick,next(tick - m_keys.last,m_vals.last))
  }
}

//****************************************************************************
