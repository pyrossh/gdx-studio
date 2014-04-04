/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.tools.particleeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.CompoundBorder;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.NumericValue;

public class ParticlePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	JPanel rowsPanel;
	JPanel editRowsPanel;
	EffectPanel effectPanel;
	private JSplitPane splitPane;
	OrthographicCamera worldCamera;
	OrthographicCamera textCamera;
	NumericValue pixelsPerMeter;
	NumericValue zoomLevel;
	NumericValue deltaMultiplier;

	float pixelsPerMeterPrev;
	float zoomLevelPrev;

	ParticleEffect effect = new ParticleEffect();
	final HashMap<ParticleEmitter, ParticleData> particleData = new HashMap<ParticleEmitter, ParticleData>();
	public ParticleRenderer particleRenderer;
	

	public ParticlePanel() {
		super(new BorderLayout());
		setVisible(true);
	}

	void reloadRows () {
		EventQueue.invokeLater(new Runnable() {
			public void run () {
				editRowsPanel.removeAll();
				addEditorRow(new NumericPanel(pixelsPerMeter, "Pixels per meter", ""));
				addEditorRow(new NumericPanel(zoomLevel, "Zoom level", ""));
				addEditorRow(new NumericPanel(deltaMultiplier, "Delta multiplier", ""));

				rowsPanel.removeAll();
				ParticleEmitter emitter = getEmitter();
				addRow(new ImagePanel(ParticlePanel.this, "Image", ""));
				addRow(new CountPanel(ParticlePanel.this, "Count",
					"Min number of particles at all times, max number of particles allowed."));
				addRow(new RangedNumericPanel(emitter.getDelay(), "Delay",
					"Time from beginning of effect to emission start, in milliseconds."));
				addRow(new RangedNumericPanel(emitter.getDuration(), "Duration", "Time particles will be emitted, in milliseconds."));
				addRow(new ScaledNumericPanel(emitter.getEmission(), "Duration", "Emission",
					"Number of particles emitted per second."));
				addRow(new ScaledNumericPanel(emitter.getLife(), "Duration", "Life", "Time particles will live, in milliseconds."));
				addRow(new ScaledNumericPanel(emitter.getLifeOffset(), "Duration", "Life Offset",
					"Particle starting life consumed, in milliseconds."));
				addRow(new RangedNumericPanel(emitter.getXOffsetValue(), "X Offset",
					"Amount to offset a particle's starting X location, in world units."));
				addRow(new RangedNumericPanel(emitter.getYOffsetValue(), "Y Offset",
					"Amount to offset a particle's starting Y location, in world units."));
				addRow(new SpawnPanel(ParticlePanel.this, emitter.getSpawnShape(), "Spawn", "Shape used to spawn particles."));
				addRow(new ScaledNumericPanel(emitter.getSpawnWidth(), "Duration", "Spawn Width",
					"Width of the spawn shape, in world units."));
				addRow(new ScaledNumericPanel(emitter.getSpawnHeight(), "Duration", "Spawn Height",
					"Height of the spawn shape, in world units."));
				addRow(new ScaledNumericPanel(emitter.getScale(), "Life", "Size", "Particle size, in world units."));
				addRow(new ScaledNumericPanel(emitter.getVelocity(), "Life", "Velocity", "Particle speed, in world units per second."));
				addRow(new ScaledNumericPanel(emitter.getAngle(), "Life", "Angle", "Particle emission angle, in degrees."));
				addRow(new ScaledNumericPanel(emitter.getRotation(), "Life", "Rotation", "Particle rotation, in degrees."));
				addRow(new ScaledNumericPanel(emitter.getWind(), "Life", "Wind", "Wind strength, in world units per second."));
				addRow(new ScaledNumericPanel(emitter.getGravity(), "Life", "Gravity", "Gravity strength, in world units per second."));
				addRow(new GradientPanel(emitter.getTint(), "Tint", ""));
				addRow(new PercentagePanel(emitter.getTransparency(), "Life", "Transparency", ""));
				addRow(new OptionsPanel(ParticlePanel.this, "Options", ""));
				for (Component component : rowsPanel.getComponents())
					if (component instanceof EditorPanel) ((EditorPanel)component).update(ParticlePanel.this);
				rowsPanel.repaint();
			}
		});
	}

	void addEditorRow (JPanel row) {
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
		editRowsPanel.add(row, new GridBagConstraints(0, -1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));
	}

	void addRow (JPanel row) {
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
		rowsPanel.add(row, new GridBagConstraints(0, -1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setVisible (String name, boolean visible) {
		for (Component component : rowsPanel.getComponents())
			if (component instanceof EditorPanel && ((EditorPanel)component).getName().equals(name)) component.setVisible(visible);
	}

	public ParticleEmitter getEmitter () {
		return effect.getEmitters().get(effectPanel.editIndex);
	}

	public ImageIcon getIcon (ParticleEmitter emitter) {
		ParticleData data = particleData.get(emitter);
		if (data == null) particleData.put(emitter, data = new ParticleData());
		String imagePath = emitter.getImagePath();
		if (data.icon == null && imagePath != null) {
			try {
				URL url;
				File file = new File(imagePath);
				if (file.exists())
					url = file.toURI().toURL();
				else {
					url = ParticlePanel.class.getResource(imagePath);
					if (url == null) return null;
				}
				data.icon = new ImageIcon(url);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
		return data.icon;
	}

	public void setIcon (ParticleEmitter emitters, ImageIcon icon) {
		ParticleData data = particleData.get(emitters);
		if (data == null) particleData.put(emitters, data = new ParticleData());
		data.icon = icon;
	}

	public void setEnabled (ParticleEmitter emitter, boolean enabled) {
		ParticleData data = particleData.get(emitter);
		if (data == null) particleData.put(emitter, data = new ParticleData());
		data.enabled = enabled;
		emitter.reset();
	}

	public boolean isEnabled (ParticleEmitter emitter) {
		ParticleData data = particleData.get(emitter);
		if (data == null) return true;
		return data.enabled;
	}

	public void initializeComponents (LwjglAWTCanvas canvas) {
		canvas.getCanvas().setPreferredSize(new Dimension(300, 250));
		// Left
		JPanel propertiesPanel = new JPanel(new BorderLayout());
		propertiesPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6), BorderFactory
			.createTitledBorder("Editor Properties")));
		editRowsPanel = new JPanel(new GridBagLayout());
		propertiesPanel.add(editRowsPanel, BorderLayout.CENTER);

		// Center
		JPanel spacer = new JPanel(new BorderLayout());
		spacer.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6), BorderFactory
				.createTitledBorder("Particle Effect")));
		spacer.add(canvas.getCanvas(), BorderLayout.CENTER);
			
		// Right
		JPanel emittersPanel = new JPanel(new BorderLayout());
		emittersPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0), BorderFactory
			.createTitledBorder("Effect Emitters")));
		effectPanel = new EffectPanel(this);
		emittersPanel.add(effectPanel, BorderLayout.CENTER);
		emittersPanel.setPreferredSize(new Dimension(330, 250));
			
			
		JPanel propertiesPanel2 = new JPanel(new BorderLayout());
		propertiesPanel2.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6), BorderFactory
			.createTitledBorder("Emitter Properties")));
		rowsPanel = new JPanel(new GridBagLayout());
		propertiesPanel2.add(rowsPanel, BorderLayout.CENTER);
				
		JPanel tool = new JPanel(new BorderLayout());
		tool.add(propertiesPanel, BorderLayout.WEST);
		tool.add(spacer, BorderLayout.CENTER);
		tool.add(emittersPanel, BorderLayout.EAST);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerSize(4);
		splitPane.add(tool, JSplitPane.TOP);
		splitPane.add(new JScrollPane(propertiesPanel2), JSplitPane.BOTTOM);
		splitPane.setDividerLocation(250);
		add(splitPane, BorderLayout.CENTER);
	}

	static class ParticleData {
		public ImageIcon icon;
		public String imagePath;
		public boolean enabled = true;
	}
}
