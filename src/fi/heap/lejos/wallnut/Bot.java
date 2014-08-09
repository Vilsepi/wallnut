package fi.heap.lejos.wallnut;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;


/**
 * Wallnut
 * 
 * @author Ville Seppanen
 *
 */

public class Bot {
	final static RegulatedMotor leftMotor = Motor.A;
	final static RegulatedMotor rightMotor = Motor.C;
	final static SensorPort touchSensorPort = SensorPort.S4;
	final static SensorPort sonicSensorPort = SensorPort.S1;
	
	final static int driveSpeed = 275;
	final static float turnRateModifier = 1;
	
	public static void main(String[] args) {
		leftMotor.setSpeed(driveSpeed);
		rightMotor.setSpeed(driveSpeed);

		Behavior[] behaviorList = {
				new DriveForward(),
				new DetectWall()
		};

		Arbitrator arbitrator = new Arbitrator(behaviorList);

		LCD.drawString("Wallnut", 0, 1);
		Button.waitForAnyPress();
		arbitrator.start();
	}
}


class DriveForward implements Behavior {
	private boolean _suppressed = false;

	public boolean takeControl() {
		return true;
	}

	public void suppress() {
		_suppressed = true;
	}

	public void action() {
		_suppressed = false;
		Bot.leftMotor.forward();
		Bot.rightMotor.forward();
		while (!_suppressed) {
			Thread.yield();
		}
		Bot.leftMotor.stop(); 
		Bot.leftMotor.stop();
	}
}


class DetectWall implements Behavior {
	private TouchSensor touch;
	private UltrasonicSensor sonar;
	
	public DetectWall() {
		touch = new TouchSensor(Bot.touchSensorPort);
		sonar = new UltrasonicSensor(Bot.sonicSensorPort);
	}

	public boolean takeControl() {
		sonar.ping();
		return touch.isPressed() || sonar.getDistance() < 30;
	}

	public void suppress() {
	}

	public void action() {
		Bot.leftMotor.rotate(-180, true);
		Bot.rightMotor.rotate((int) (-180+(-180*Bot.turnRateModifier)));
	}
}

