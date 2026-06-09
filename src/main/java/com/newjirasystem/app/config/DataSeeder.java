package com.newjirasystem.app.config;

import com.newjirasystem.app.comentarios.Comentario;
import com.newjirasystem.app.comentarios.ComentarioRepository;
import com.newjirasystem.app.proyectos.Proyecto;
import com.newjirasystem.app.proyectos.ProyectosRepository;
import com.newjirasystem.app.tickets.*;
import com.newjirasystem.app.usuarios.Rol;
import com.newjirasystem.app.usuarios.Usuario;
import com.newjirasystem.app.usuarios.UsuariosRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuariosRepository usuarioRepository;
    private final ProyectosRepository proyectoRepository;
    private final TicketsRepository ticketRepository;
    private final ComentarioRepository comentarioRepository;

    public DataSeeder(UsuariosRepository usuarioRepository,
                      ProyectosRepository proyectoRepository,
                      TicketsRepository ticketRepository,
                      ComentarioRepository comentarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.ticketRepository = ticketRepository;
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    @Transactional // Asegura la sesión de Hibernate para manejar las claves foráneas
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Faker faker = new Faker();

        List<Usuario> usuarios = crearUsuarios(faker);
        List<Proyecto> proyectos = crearProyectos(faker);
        List<Ticket> tickets = crearTickets(faker, usuarios, proyectos);

        crearComentarios(faker, usuarios, tickets);

        System.out.println("Datos de prueba generados correctamente.");
    }

    private List<Usuario> crearUsuarios(Faker faker) {
        List<Usuario> usuarios = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            String nombre = faker.name().fullName();
            String telefono = faker.phoneNumber().cellPhone();
            Rol rol = i % 5 == 0 ? Rol.ADMIN : Rol.USER;

            // Uso del constructor: Usuario(Long id, String nombre, Rol rol, String telefono)
            // Pasamos 'null' en el ID para que la base de datos lo autogenerara con el @Id @GeneratedValue
            Usuario usuario = new Usuario(nombre, rol, telefono);
            usuarios.add(usuario);
        }

        return usuarioRepository.saveAll(usuarios);
    }

    private List<Proyecto> crearProyectos(Faker faker) {
        List<Proyecto> proyectos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String nombre = faker.company().name();
            String codigo = "PROJ-" + (i + 1);

            // Uso del constructor: Proyecto(String nombre, String codigoProyecto)
            Proyecto proyecto = new Proyecto(nombre, codigo);
            proyectos.add(proyecto);
        }

        return proyectoRepository.saveAll(proyectos);
    }

    private List<Ticket> crearTickets(Faker faker, List<Usuario> usuarios, List<Proyecto> proyectos) {
        Random random = new Random();
        List<Ticket> tickets = new ArrayList<>();

        PrioridadTicket[] prioridades = PrioridadTicket.values();
        TipoTicket[] tipos = TipoTicket.values();
        EstadoTicket[] estados = EstadoTicket.values();

        for (int i = 0; i < 30; i++) {
            String clave = "TICKET-" + (i + 1);
            TipoTicket tipoRandom = tipos[random.nextInt(tipos.length)];
            PrioridadTicket prioridadRandom = prioridades[random.nextInt(prioridades.length)];
            Proyecto proyectoRandom = proyectos.get(random.nextInt(proyectos.size()));
            Usuario creadorRandom = usuarios.get(random.nextInt(usuarios.size()));
            String titulo = faker.book().title();
            String descripcion = faker.lorem().characters(100);

            // Uso del constructor completo de Ticket
            Ticket ticket = new Ticket(
                    clave,
                    tipoRandom,
                    prioridadRandom,
                    proyectoRandom,
                    creadorRandom,
                    titulo,
                    descripcion
            );

            // Opcional: Como tu constructor setea por defecto "POR_HACER", si quieres que el seeder
            // tenga estados variados y realistas, usamos el setter para aleatorizarlo después de construirlo:
            ticket.setEstado(estados[random.nextInt(estados.length)]);

            // Asignamos un desarrollador aleatorio de tu lista de usuarios
            ticket.setAsignado(usuarios.get(random.nextInt(usuarios.size())));

            tickets.add(ticket);
        }

        return ticketRepository.saveAll(tickets);
    }

    private void crearComentarios(Faker faker, List<Usuario> usuarios, List<Ticket> tickets) {
        Random random = new Random();
        List<Comentario> comentarios = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            String texto = faker.lorem().paragraph();
            Ticket ticketRandom = tickets.get(random.nextInt(tickets.size()));
            Usuario autorRandom = usuarios.get(random.nextInt(usuarios.size()));

            // Uso del constructor: Comentario(String texto, Ticket ticket, Usuario autor)
            Comentario comentario = new Comentario(texto, ticketRandom, autorRandom);
            comentarios.add(comentario);
        }

        comentarioRepository.saveAll(comentarios);
    }
}