import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import org.json.simple.parser.ParseException;

public class Servidor {
    private static final int PORT = 33333;
    private LivroDAO livroDAO = new LivroDAO();

    public static void main(String[] args) {
        new Servidor().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado na porta " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    System.out.println("Erro ao aceitar cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {

                String command;
                while ((command = (String) input.readObject()) != null) {
                    System.out.println("Comando recebido: " + command);
                    switch (command) {
                        case "LISTAR":
                            List<Livro> livros = livroDAO.listarLivros();
                            System.out.println("Enviando lista de livros para o cliente.");
                            output.writeObject(livros);
                            output.flush();
                            System.out.println("Livros enviados para o cliente.");
                            output.writeObject("OK");
                            output.flush();
                            break;
                        case "CADASTRAR":
                            Livro novoLivro = (Livro) input.readObject();
                            cadastrarLivro(novoLivro);
                            output.writeObject("Livro cadastrado com sucesso");
                            output.flush();
                            output.writeObject("OK");
                            output.flush();
                            break;
                        case "ALUGAR":
                            String tituloLivroAlugar = (String) input.readObject();
                            String respostaAluguel = alugarLivro(tituloLivroAlugar);
                            output.writeObject(respostaAluguel);
                            output.flush();
                            output.writeObject("OK");
                            output.flush();
                            break;
                        case "DEVOLVER":
                            String tituloLivroDevolver = (String) input.readObject();
                            devolverLivro(tituloLivroDevolver);
                            output.writeObject("Livro devolvido com sucesso");
                            output.flush();
                            output.writeObject("OK");
                            output.flush();
                            break;
                        case "REMOVER":
                            String tituloLivroRemover = (String) input.readObject();
                            String respostaRemover = removerLivro(tituloLivroRemover);
                            output.writeObject(respostaRemover);
                            output.flush();
                            output.writeObject("OK");
                            output.flush();
                            break;
                        default:
                            output.writeObject("Comando desconhecido");
                            output.flush();
                            output.writeObject("OK");
                            output.flush();
                    }
                    System.out.println("Comando processado: " + command);
                }
            } catch (IOException | ClassNotFoundException | ParseException e) {
                System.out.println("Erro ao processar comando: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }

        private void cadastrarLivro(Livro livro) throws IOException, ParseException {
            List<Livro> livros = livroDAO.listarLivros();
            livros.add(livro);
            livroDAO.salvarLivros(livros);
        }

        private String alugarLivro(String tituloLivro) throws IOException, ParseException {
            List<Livro> livros = livroDAO.listarLivros();
            for (Livro livro : livros) {
                if (livro.getTitulo().equals(tituloLivro)) {
                    if (livro.getExemplares() > 0) {
                        livro.setExemplares(livro.getExemplares() - 1);
                        livroDAO.salvarLivros(livros);
                        return "Livro alugado com sucesso";
                    } else {
                        return "Nenhum exemplar disponível para aluguel";
                    }
                }
            }
            return "Livro não encontrado";
        }

        private void devolverLivro(String tituloLivro) throws IOException, ParseException {
            List<Livro> livros = livroDAO.listarLivros();
            for (Livro livro : livros) {
                if (livro.getTitulo().equals(tituloLivro)) {
                    livro.setExemplares(livro.getExemplares() + 1);
                    livroDAO.salvarLivros(livros);
                    break;
                }
            }
        }

        private String removerLivro(String tituloLivro) throws IOException, ParseException {
            List<Livro> livros = livroDAO.listarLivros();
            for (int i = 0; i < livros.size(); i++) {
                if (livros.get(i).getTitulo().equals(tituloLivro)) {
                    livros.remove(i);
                    livroDAO.salvarLivros(livros);
                    return "Livro removido com sucesso";
                }
            }
            return "Livro não encontrado";
        }
    }
}
