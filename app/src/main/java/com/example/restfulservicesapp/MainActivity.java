
package com.example.restfulservicesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restfulservicesapp.api.ApiService;
import com.example.restfulservicesapp.api.RetrofitClient;
import com.example.restfulservicesapp.model.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private Button btnGet;
    private Button btnPost;
    private EditText etTitle;
    private EditText etBody;
    private TextView tvResult;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conectar las vistas del XML
        btnGet = findViewById(R.id.btnGet);
        btnPost = findViewById(R.id.btnPost);
        etTitle = findViewById(R.id.etTitle);
        etBody = findViewById(R.id.etBody);
        tvResult = findViewById(R.id.tvResult);
        progressBar = findViewById(R.id.progressBar);

        // Crear el servicio Retrofit
        apiService = RetrofitClient.getApiService();

        // Botón GET
        btnGet.setOnClickListener(v -> fetchReports());

        // Botón POST
        btnPost.setOnClickListener(v -> createReport());
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        btnGet.setEnabled(!loading);
        btnPost.setEnabled(!loading);
    }

    // Consultar publicaciones con GET
    private void fetchReports() {
        showLoading(true);
        tvResult.setText("Consultando publicaciones...");

        apiService.getAllReports().enqueue(
                new Callback<List<Report>>() {

                    @Override
                    public void onResponse(
                            Call<List<Report>> call,
                            Response<List<Report>> response) {

                        showLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Report> reports = response.body();
                            StringBuilder result = new StringBuilder();

                            result.append("Publicaciones recibidas: ")
                                    .append(reports.size())
                                    .append("\n\n");

                            int limit = Math.min(reports.size(), 10);

                            for (int i = 0; i < limit; i++) {
                                Report report = reports.get(i);

                                result.append("ID: ")
                                        .append(report.getId())
                                        .append("\nTítulo: ")
                                        .append(report.getTitle())
                                        .append("\nContenido: ")
                                        .append(report.getBody())
                                        .append("\n\n");
                            }

                            tvResult.setText(result.toString());

                        } else {
                            tvResult.setText(
                                    "Error del servidor. Código HTTP: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Report>> call,
                            Throwable t) {

                        showLoading(false);

                        tvResult.setText(
                                "No se pudo conectar. Comprueba " +
                                        "tu conexión a Internet e inténtalo de nuevo."
                        );

                        Toast.makeText(
                                MainActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Enviar una publicación con POST
    private void createReport() {
        String title = etTitle.getText().toString().trim();
        String body = etBody.getText().toString().trim();

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(
                    this,
                    "Escribe un título y un contenido.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Report newReport = new Report(title, body, 1);

        showLoading(true);
        tvResult.setText("Enviando publicación...");

        apiService.createReport(newReport).enqueue(
                new Callback<Report>() {

                    @Override
                    public void onResponse(
                            Call<Report> call,
                            Response<Report> response) {

                        showLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Report created = response.body();

                            tvResult.setText(
                                    "Publicación enviada correctamente.\n\n"
                                            + "ID recibido: " + created.getId()
                                            + "\nTítulo: " + created.getTitle()
                                            + "\nContenido: " + created.getBody()
                            );

                            Toast.makeText(
                                    MainActivity.this,
                                    "POST realizado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {
                            tvResult.setText(
                                    "Error al enviar. Código HTTP: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Report> call,
                            Throwable t) {

                        showLoading(false);

                        tvResult.setText(
                                "Falló el envío. Comprueba Internet e inténtalo de nuevo."
                        );

                        Toast.makeText(
                                MainActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}
