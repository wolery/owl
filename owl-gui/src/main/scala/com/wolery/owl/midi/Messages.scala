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
//*  See Also: http://www.somascape.org/midi/tech/mfile.html
//*
//*
//****************************************************************************

package com.wolery.owl.midi

//****************************************************************************

import java.io._
import Math.{max}
import scala.language.postfixOps

import com.wolery.owl.core._
import com.wolery.owl.core.utilities._
import com.wolery.owl.Tempo

import javax.sound.midi.{ MetaMessage, ShortMessage }

//****************************************************************************

object messages
{
//Meta Events

  val SEQUENCE:      Byte = 0x00
  val TEXT:          Byte = 0x01
  val COPYRIGHT:     Byte = 0x02
  val TRACK:         Byte = 0x03
  val INSTRUMENT:    Byte = 0x04
  val LYRIC:         Byte = 0x05
  val MARKER:        Byte = 0x06
  val CUE:           Byte = 0x07
  val PROGRAM:       Byte = 0x08
  val DEVICE:        Byte = 0x09
  val CHANNEL:       Byte = 0x20
  val PORT:          Byte = 0x21
  val END:           Byte = 0x2F
  val TEMPO:         Byte = 0x51
  val SMPTE:         Byte = 0x54
  val METER:         Byte = 0x58
  val KEY:           Byte = 0x59
  val CUSTOM:        Byte = 0x7F

//Owl Events

  val SCALE:         Byte = 0x60
  val STRING:        Byte = 0x61
  val POSITION:      Byte = 0x62

  implicit final class ShortMessageEx(val m: ShortMessage) extends AnyVal
  {
    def isChannelMessage: Bool  = m.getCommand != 0xF0
    def isSystemMessage : Bool  = m.getCommand == 0xF0
    def pitch           : Pitch = Pitch(m.getData1 & 0xFF)
    def integer         : ℕ     = (m.getData1 & 0x7F) | ((m.getData2 & 0x7F) << 7)
  }

  implicit final class MetaMessageEx(val m: MetaMessage) extends AnyVal
  {
    def string: String = new String(m.getData)

    def int8  : ℤ = m.getData.apply(0)
    def uint4 : ℕ =  uint8(0) & 0x0F
    def uint7 : ℕ =  uint8(0) & 0x7F
    def uint8 : ℕ =  uint8(0) & 0xFF
    def uint16: ℕ =                    (uint8(0) << 8) | uint8(1)
    def uint24: ℕ = (uint8(0) << 16) | (uint8(1) << 8) | uint8(2)

    def uint8(index: ℕ = 0): ℕ =
    {
      assert(isBetween(index,0,m.getLength))

      m.getData.apply(index) & 0xFF
    }

    def tempo: Tempo =
    {
      assert(m.getType == TEMPO)

      60e6 / max(uint24,0.1)
    }

    def smpte: (ℕ,ℕ,ℕ,ℕ,ℕ) =
    {
      assert(m.getType == SMPTE)

      (uint8(0),uint8(1),uint8(2),uint8(3),uint8(4))
    }

    def meter: Meter =
    {
      assert(m.getType == METER)

      Meter(uint8(0),1 << uint8(1),uint8(2),uint8(3))
    }

    def key: Scale =
    {
      assert(m.getType == KEY)

      val keys = Seq(C♭,G♭,D♭,A♭,E♭,B♭,F,C,G,D,A,E,B,F♯,C♯)
      val root = keys(7 + m.int8)
      val mode = uint8(1) match
      {
        case 0 ⇒ "ionian"
        case _ ⇒ "aeolean"
      }

      Scale(root,mode).get
    }

    def scale:Scale = anything[Scale](SCALE)
  //def string: ℕ   = anything[ℕ](STRING)
    def position: ℕ = anything[ℕ](POSITION)

    private
    def anything[α](byte: Byte): α =
    {
      assert(m.getType == byte)
      val b = new ByteArrayInputStream(m.getData)
      val i = new ObjectInputStream(b)

      val t = i.readObject.asInstanceOf[α]
      i.close
      t
    }
  }

  private
  def create[α](byte: Byte,any: α): MetaMessage =
  {
    val b = new ByteArrayOutputStream()
    val o = new ObjectOutputStream(b)

    o.writeObject(any)
    o.close

    new MetaMessage(byte,b.toByteArray,b.size)
  }

  def scale(scale: Scale):   MetaMessage = create[Scale](SCALE,scale)
  def string(string: ℕ):     MetaMessage = create[ℕ]    (STRING,string)
  def position(position: ℕ): MetaMessage = create[ℕ]    (POSITION,position)
}
//****************************************************************************
