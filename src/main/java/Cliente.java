import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Cliente {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 33333;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Conectado ao servidor.");

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Listar livros");
                System.out.println("2. Cadastrar novo livro");
                System.out.println("3. Alugar livro");
                System.out.println("4. Devolver livro");
                System.out.println("5. Sair");
                System.out.print("Escolha uma opção: ");
                int escolha = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (escolha) {
                    case 1:
                        listarLivros(output, input);
                        break;
                    case 2:
                        cadastrarLivro(output, input);
                        break;
                    case 3:
                        alugarLivro(output, input);
                        break;
                    case 4:
                        devolverLivro(output, input);
                        break;
                    case 5:
                        System.out.println("Saindo...");
                        return;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void listarLivros(ObjectOutputStream output, ObjectInputStream input) throws IOException, ClassNotFoundException {
        output.writeObject("LISTAR");
        output.flush();
        System.out.println("Comando LISTAR enviado.");
        List<Livro> livros = (List<Livro>) input.readObject();
        System.out.println("Resposta recebida do servidor:");
        for (Livro livro : livros) {
            System.out.println(livro);
        }
        String confirmacao = (String) input.readObject();
        if (!"OK".equals(confirmacao)) {
            throw new IOException("Erro na confirmação de recebimento");
        }
    }

    private static void cadastrarLivro(ObjectOutputStream output, ObjectInputStream input) throws IOException, ClassNotFoundException {
        System.out.print("Digite o título do livro: ");
        String titulo = scanner.nextLine();
        System.out.print("Digite o autor do livro: ");
        String autor = scanner.nextLine();
        System.out.print("Digite o gênero do livro: ");
        String genero = scanner.nextLine();
        System.out.print("Digite o número de exemplares: ");
        int exemplares = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        output.writeObject("CADASTRAR");
        output.writeObject(new Livro(titulo, autor, genero, exemplares));
        output.flush();
        System.out.println("Comando CADASTRAR enviado.");
        String respostaCadastro = (String) input.readObject();
        System.out.println("Resposta recebida do servidor: " + respostaCadastro);
        String confirmacao = (String) input.readObject();
        if (!"OK".equals(confirmacao)) {
            throw new IOException("Erro na confirmação de recebimento");
        }
    }

    private static void alugarLivro(ObjectOutputStream output, ObjectInputStream input) throws IOException, ClassNotFoundException {
        System.out.print("Digite o título do livro para alugar: ");
        String titulo = scanner.nextLine();

        output.writeObject("ALUGAR");
        output.writeObject(titulo);
        output.flush();
        System.out.println("Comando ALUGAR enviado.");
        String respostaAluguel = (String) input.readObject();
        System.out.println("Resposta recebida do servidor: " + respostaAluguel);
        String confirmacao = (String) input.readObject();
        if (!"OK".equals(confirmacao)) {
            throw new IOException("Erro na confirmação de recebimento");
        }
    }

    private static void devolverLivro(ObjectOutputStream output, ObjectInputStream input) throws IOException, ClassNotFoundException {
        System.out.print("Digite o título do livro para devolver: ");
        String titulo = scanner.nextLine();

        output.writeObject("DEVOLVER");
        output.writeObject(titulo);
        output.flush();
        System.out.println("Comando DEVOLVER enviado.");
        String respostaDevolucao = (String) input.readObject();
        System.out.println("Resposta recebida do servidor: " + respostaDevolucao);
        String confirmacao = (String) input.readObject();
        if (!"OK".equals(confirmacao)) {
            throw new IOException("Erro na confirmação de recebimento");
        }
    }
}
