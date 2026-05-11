package com.example.sansimap;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HorarioApp extends JPanel {
    private static final String NOMBRE_ARCHIVO = "cursos_horarios.txt";

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SearchPanel searchPanel;
    private DetailPanel detailPanel;
    private ArrayList<Course> courses;

    public HorarioApp() {
        setLayout(new BorderLayout());

        initData();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        searchPanel = new SearchPanel(courses, this);
        detailPanel = new DetailPanel(this);

        mainPanel.add(searchPanel, "search");
        mainPanel.add(detailPanel, "detail");

        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "search");

        revalidate();
        repaint();
    }

    private void initData() {
        courses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(NOMBRE_ARCHIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 1) {
                    String aulaName = parts[0];
                    Course course = new Course(aulaName, aulaName);

                    for (int i = 1; i < parts.length; i++) {
                        String[] scheduleParts = parts[i].split(",");
                        if (scheduleParts.length >= 2) {
                            String day = scheduleParts[0];
                            String[] intervals = new String[scheduleParts.length - 1];
                            System.arraycopy(scheduleParts, 1, intervals, 0, intervals.length);
                            course.addSchedule(day, intervals);
                        }
                    }
                    courses.add(course);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el archivo: " + NOMBRE_ARCHIVO,
                    "Error de Archivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDetails(Course course) {
        detailPanel.setCourse(course);
        cardLayout.show(mainPanel, "detail");
        revalidate();
        repaint();
    }

    public void backToSearch() {
        cardLayout.show(mainPanel, "search");
        revalidate();
        repaint();
    }

    private static class TimeSlot {
        String day;
        List<String> intervals;

        public TimeSlot(String day, String... intervals) {
            this.day = day;
            this.intervals = List.of(intervals);
        }
    }

    private static class Course {
        String name;
        String classroom;
        List<TimeSlot> schedules;

        public Course(String name, String classroom) {
            this.name = name;
            this.classroom = classroom;
            this.schedules = new ArrayList<>();
        }

        public void addSchedule(String day, String... intervals) {
            schedules.add(new TimeSlot(day, intervals));
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            Color color1 = new Color(10, 30, 80);
            Color color2 = new Color(200, 30, 40);
            GradientPaint gp = new GradientPaint(0, 0, color1, width, 0, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    private static class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBackground(Color.WHITE);
            setBorder(new MatteBorder(0, 0, 5, 0, new Color(0, 0, 0, 50)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, getWidth(), getHeight() - 5, cornerRadius, cornerRadius);
        }
    }

    private static class SearchPanel extends JPanel {
        private JTextField searchField;
        private JButton searchButton;
        private HorarioApp app;
        private ArrayList<Course> courses;

        public SearchPanel(ArrayList<Course> courses, HorarioApp app) {
            this.app = app;
            this.courses = courses;
            setLayout(new GridBagLayout());
            setBackground(new Color(240, 240, 240));

            JPanel container = new JPanel(new GridLayout(3, 1, 10, 10));
            container.setOpaque(false);

            JLabel titleLabel = new JLabel("Ingrese el nombre del AULA:", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            container.add(titleLabel);

            searchField = new JTextField(20);
            searchField.setFont(new Font("Arial", Font.PLAIN, 16));
            container.add(searchField);

            searchButton = new JButton("Buscar Aula");
            searchButton.setFont(new Font("Arial", Font.BOLD, 16));
            searchButton.setPreferredSize(new Dimension(150, 40));
            searchButton.addActionListener(e -> {
                String searchText = searchField.getText().trim();
                if (searchText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, escriba un aula.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Course foundCourse = null;
                for (Course c : courses) {
                    if (c.classroom.equalsIgnoreCase(searchText)) {
                        foundCourse = c;
                        break;
                    }
                }

                if (foundCourse != null) {
                    app.showDetails(foundCourse);
                } else {
                    JOptionPane.showMessageDialog(this, "Aula no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            searchField.addActionListener(e -> searchButton.doClick());
            container.add(searchButton);

            add(container);
        }
    }

    private static class DetailPanel extends JPanel {
        private HorarioApp app;
        private JPanel scheduleContentPanel;
        private JLabel roomLabel;
        private Course currentCourse;

        public DetailPanel(HorarioApp app) {
            this.app = app;
            setLayout(new BorderLayout());
            GradientPanel background = new GradientPanel();
            background.setLayout(new BorderLayout());

            JPanel mainContent = new JPanel();
            mainContent.setOpaque(false);
            mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
            mainContent.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

            RoundedPanel roomTitlePanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            roomTitlePanel.setMaximumSize(new Dimension(400, 70));
            roomLabel = new JLabel("AULA", JLabel.CENTER);
            roomLabel.setFont(new Font("Arial", Font.BOLD, 28));
            roomTitlePanel.add(roomLabel);
            mainContent.add(roomTitlePanel);
            mainContent.add(Box.createVerticalStrut(30));

            scheduleContentPanel = new JPanel();
            scheduleContentPanel.setOpaque(false);
            scheduleContentPanel.setLayout(new BoxLayout(scheduleContentPanel, BoxLayout.Y_AXIS));

            JScrollPane scrollPane = new JScrollPane(scheduleContentPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            mainContent.add(scrollPane);

            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            footerPanel.setOpaque(false);
            JButton backButton = new JButton("Volver");
            backButton.addActionListener(e -> app.backToSearch());
            footerPanel.add(backButton);

            background.add(mainContent, BorderLayout.CENTER);
            background.add(footerPanel, BorderLayout.SOUTH);
            add(background, BorderLayout.CENTER);
        }

        public void setCourse(Course course) {
            this.currentCourse = course;
            roomLabel.setText(course.classroom);
            renderSchedules();
        }

        private void renderSchedules() {
            scheduleContentPanel.removeAll();
            for (TimeSlot slot : currentCourse.schedules) {
                RoundedPanel rowPanel = new RoundedPanel(new GridBagLayout());
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(0, 15, 0, 15);

                JLabel dayLabel = new JLabel(slot.day + " : ");
                dayLabel.setFont(new Font("Arial", Font.BOLD, 22));
                gbc.gridx = 0;
                rowPanel.add(dayLabel, gbc);

                JPanel slotsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
                slotsContainer.setOpaque(false);
                for (String interval : slot.intervals) {
                    JLabel timeInterval = new JLabel(interval);
                    timeInterval.setFont(new Font("Arial", Font.BOLD, 20));
                    slotsContainer.add(timeInterval);
                }
                gbc.gridx = 1;
                gbc.weightx = 1.0;
                rowPanel.add(slotsContainer, gbc);

                scheduleContentPanel.add(rowPanel);
                scheduleContentPanel.add(Box.createVerticalStrut(15));
            }
            scheduleContentPanel.revalidate();
            scheduleContentPanel.repaint();
        }
    }
}