package com.tt1;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio principal de gestión de tareas ToDo.
 * Permite crear tareas, registrar emails de notificación, marcar tareas
 * como finalizadas y listar las pendientes. Además, comprueba automáticamente
 * si hay tareas vencidas y envía alertas por correo electrónico.
 *
 * @author Clara
 * @version 1.0
 */
public class Servicio {
    private IRepositorio repositorio;
    private IMailerStub mailer;

    /**
     * Constructor del servicio de tareas.
     * @param repositorio repositorio donde se almacenan y consultan las tareas y emails
     * @param mailer servicio de envío de correos electrónicos para las notificaciones
     */
    public Servicio(IRepositorio repositorio, IMailerStub mailer) {
        this.repositorio = repositorio;
        this.mailer = mailer;
    }

    /**
     * Crea una nueva tarea con el nombre y fecha límite indicados, la guarda
     * en el repositorio y comprueba si hay tareas vencidas para alertar.
     * @param nombre nombre identificativo de la nueva tarea
     * @param fechaLimite fecha límite para completar la tarea
     */
    public void crearToDo(String nombre, LocalDate fechaLimite) {
        ToDo todo = new ToDo();
        todo.setNombre(nombre);
        todo.setFechaLimite(fechaLimite);
        repositorio.guardarToDo(todo);
        comprobarVencidosYAlertar();
    }

    /**
     * Registra una dirección de email para recibir notificaciones de tareas vencidas
     * y comprueba si hay tareas vencidas para alertar.
     * @param email dirección de correo electrónico a registrar
     */
    public void agregarEmail(String email) {
        repositorio.guardarEmail(email);
        comprobarVencidosYAlertar();
    }

    /**
     * Marca una tarea existente como finalizada y comprueba si hay tareas
     * vencidas para alertar.
     * @param nombre nombre de la tarea a marcar como finalizada
     */
    public void marcarComoFinalizada(String nombre) {
        repositorio.marcarCompletado(nombre);
        comprobarVencidosYAlertar();
    }

    /**
     * Devuelve la lista de tareas pendientes (no completadas).
     * Antes de devolver la lista, comprueba si hay tareas vencidas para alertar.
     * @return lista de objetos {@link ToDo} que aún no han sido completados
     */
    public List<ToDo> listarPendientes() {
        comprobarVencidosYAlertar();
        return repositorio.obtenerPendientes();
    }

    /**
     * Comprueba si alguna tarea pendiente ha superado su fecha límite y,
     * en tal caso, envía un correo de alerta a todos los emails registrados.
     */
    private void comprobarVencidosYAlertar() {
        List<ToDo> pendientes = repositorio.obtenerPendientes();
        List<String> emails = repositorio.obtenerEmails();

        for (ToDo t : pendientes) {
            if (t.getFechaLimite().isBefore(LocalDate.now())) {
                for (String email : emails) {
                    mailer.enviarCorreo(email, "Tarea vencida: " + t.getNombre());
                }
            }
        }
    }
}