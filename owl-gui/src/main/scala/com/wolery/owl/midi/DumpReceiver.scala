//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Displays the file format information of a MIDI file.
//*
//*
//*  See Also: https://www.midi.org/specifications/item/table-1-summary-of-midi-message
//*            http://www.somascape.org/midi/tech/mfile.html
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.midi

//****************************************************************************

import java.io.PrintStream
import javax.sound.midi._
import com.wolery.owl.core._
import messages._
import javax.sound.midi.ShortMessage._

//****************************************************************************

final class DumpReceiver(m_out: PrintStream = System.out) extends Receiver
{
  def close(): Unit =
  {}

  def send(mm: MidiMessage,ts: Long): Unit = synchronized
  {
    if (ts != -1)
    {
      m_out.print(s"$ts ")
    }

    if (true)
    {
      m_out.print(hex(mm))
      m_out.print(' ')
    }

    mm match
    {
      case m: ShortMessage if m.isChannelMessage ⇒ onChannelMessage(m)
      case m: ShortMessage                       ⇒ onSystemMessage(m)
      case m: MetaMessage                        ⇒ onMetaMessage(m)
      case m: SysexMessage                       ⇒ onSysexMessage(m)
      case _                                     ⇒ m_out.print("unknown message type")
    }

    m_out.println()
  }

  def onChannelMessage(mm: ShortMessage): Unit =
  {
    assert(mm.isChannelMessage)

    def note = f"${mm.pitch.toString}%-3s"
    def val1 = f"${mm.getData1}%03d"
    def val2 = f"${mm.getData2}%03d"

    m_out.print(f"ch ${mm.getChannel + 1}%02d ")

    mm.getCommand match
    {
      case NOTE_OFF         ⇒ m_out.print(s"note-off   $note ($val2)")
      case NOTE_ON          ⇒ m_out.print(s"note-on    $note ($val2)")
      case POLY_PRESSURE    ⇒ m_out.print(s"p-pressure $note ($val2)")
      case CONTROL_CHANGE   ⇒ m_out.print(s"controller $val1 ($val2)")
      case PROGRAM_CHANGE   ⇒ m_out.print(s"program    $val1")
      case CHANNEL_PRESSURE ⇒ m_out.print(s"c-pressure $note")
      case PITCH_BEND       ⇒ m_out.print(s"pitch-bend ${mm.integer}")
      case _                ⇒ m_out.print("unknown message")
    }
  }

  def onSystemMessage(mm: ShortMessage): Unit =
  {
    assert(mm.isSystemMessage)

    def song = f"${mm.getData1}%03d"

    def mtcQuarterFrame =
    {
      mm.getData1 & 0x70 match
      {
        case 0x00 ⇒ m_out.print("frame count LS:   ")
        case 0x10 ⇒ m_out.print("frame count MS:   ")
        case 0x20 ⇒ m_out.print("seconds count LS: ")
        case 0x30 ⇒ m_out.print("seconds count MS: ")
        case 0x40 ⇒ m_out.print("minutes count LS: ")
        case 0x50 ⇒ m_out.print("minutes count MS: ")
        case 0x60 ⇒ m_out.print("hours count LS:   ")
        case 0x70 ⇒ m_out.print("hours count MS:   ")
      }

      m_out.print(mm.getData1 & 0x0F)
    }

    mm.getStatus & 0x0F match
    {
      case 0x0 ⇒ m_out.print(s"sysex[           ")
      case 0x1 ⇒ m_out.print(s"mtc 1/4 frame $mtcQuarterFrame")
      case 0x2 ⇒ m_out.print(s"song position ${mm.integer}")
      case 0x3 ⇒ m_out.print(s"song select $song")
      case 0x6 ⇒ m_out.print(s"tune request     ")
      case 0x7 ⇒ m_out.print(s"sysex]           ")
      case 0x8 ⇒ m_out.print(s"timing clock     ")
      case 0xA ⇒ m_out.print(s"start            ")
      case 0xB ⇒ m_out.print(s"continue         ")
      case 0xC ⇒ m_out.print(s"stop             ")
      case 0xE ⇒ m_out.print(s"active sensing   ")
      case 0xF ⇒ m_out.print(s"system reset     ")
      case  _  ⇒ m_out.print(s"?")
    }
  }

  def onMetaMessage(mm: MetaMessage): Unit =
  {
    def print(s:String,v: Any = "") = m_out.print(s + " " + v)

    mm.getType match
    {
      case SEQUENCE  ⇒ print("sequence-number   ",mm.uint16)
      case TEXT      ⇒ print("text              ",mm.string)
      case COPYRIGHT ⇒ print("copyright         ",mm.string)
      case TRACK     ⇒ print("title             ",mm.string)
      case INSTRUMENT⇒ print("instrument        ",mm.string)
      case LYRIC     ⇒ print("lyric             ",mm.string)
      case MARKER    ⇒ print("marker            ",mm.string)
      case CUE       ⇒ print("cue               ",mm.string)
      case PROGRAM   ⇒ print("program           ",mm.string)
      case DEVICE    ⇒ print("device            ",mm.string)
      case CHANNEL   ⇒ print("channel           ",mm.uint4)
      case PORT      ⇒ print("port              ",mm.uint7)
      case END       ⇒ print("end-of-track      ")
      case TEMPO     ⇒ print("tempo             ",mm.tempo)
      case SMPTE     ⇒ print("smpte-offset      ",mm.smpte)
      case METER     ⇒ print("time-signature    ",mm.meter)
      case KEY       ⇒ print("key-signature     ",mm.key)
      case 0x7F      ⇒ print("sequencer-specific",hex(mm))
      case _         ⇒ print("unknown meta event",hex(mm))
    }
  }

  def onSysexMessage(mm: SysexMessage): Unit =
  {
    m_out.print("sysex " + hex(mm,1,10))
  }

  def hex(mm: MidiMessage,min: ℕ = 3,max: ℕ = 3): String =
  {
    require(0<min && min <= max)

    val b = mm.getMessage.take(max)
    val n = mm.getLength
    val s = new StringBuffer(3 * (max+1) + 2)

    s.append(s"${Console.CYAN}[")

    for (i ← 0 until 1)
    {
      s.append(f"${b(i)}%02X")
    }

    for (i ← 1 until b.size)
    {
      s.append(f" ${b(i)}%02X")
    }

    for (i ← b.size until min)
    {
      s.append("   ")
    }

    if (max < n)
    {
      s.append('…')
    }
    else
    {
      s.append(']')
    }

    s.append(s"${Console.RESET}")
    s.toString
  }
}

//****************************************************************************
