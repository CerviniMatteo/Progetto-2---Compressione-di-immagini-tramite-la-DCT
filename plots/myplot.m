clear; clc; close all;

%% === LOAD DATA ===
CSV = readtable("times_vs_size.csv", 'VariableNamingRule', 'preserve');

CSV.Properties.VariableNames = matlab.lang.makeValidName(CSV.Properties.VariableNames);

CSV.Properties.VariableNames = strrep(CSV.Properties.VariableNames, 'MyDCTTime_ms_', 'my_time');
CSV.Properties.VariableNames = strrep(CSV.Properties.VariableNames, 'LibDCTTime_ms_', 'lib_time');
CSV.Properties.VariableNames = strrep(CSV.Properties.VariableNames, 'Ratio_Lib_My_', 'ratio');
CSV.Properties.VariableNames = strrep(CSV.Properties.VariableNames, 'Size', 'n');

n = CSV.n;

%% === THEORETICAL COMPLEXITY MODELS ===
theory_n     = n;
theory_n2    = n.^2;
theory_n2log = n.^2 .* log2(n);
theory_n3    = n.^3;

%% =========================================================
% 1) EXPERIMENTAL DATA - logarithmic y-scale representation
%% =========================================================
f1 = figure;

semilogy(n, CSV.my_time, '-o', 'LineWidth', 1.5);
hold on
semilogy(n, CSV.lib_time, '-s', 'LineWidth', 1.5);
hold off

xlabel('Matrix size n');
ylabel('Execution time (ms, log-scale)');
title('Experimental execution time with logarithmic y-axis scaling');

legend('My DCT-II', 'JTransform DCT-II', 'Location', 'northwest');
grid on;

%% =========================================================
% 2) EXPERIMENTAL DATA - linear scale with bounded y-axis range
%% =========================================================
f2 = figure;

plot(n, CSV.my_time, '-o', 'LineWidth', 1.5);
hold on
plot(n, CSV.lib_time, '-s', 'LineWidth', 1.5);
hold off

xlabel('Matrix size n');
ylabel('Execution time (ms)');
title('Experimental execution time with linear scaling and y-range constraint');

legend('My DCT-II', 'JTransform DCT-II', 'Location', 'northwest');
grid on;

ylim([0 max(CSV.my_time) * 1.1]);

%% =========================================================
% 3) EXPERIMENTAL DATA + THEORETICAL MODELS - log-scale comparison
%% =========================================================
f3 = figure;

semilogy(n, CSV.my_time, '-o', 'LineWidth', 1.5);
hold on
semilogy(n, CSV.lib_time, '-s', 'LineWidth', 1.5);

semilogy(n, theory_n, '--', 'LineWidth', 1.2);
semilogy(n, theory_n2, '--', 'LineWidth', 1.2);
semilogy(n, theory_n2log, '--', 'LineWidth', 1.2);
semilogy(n, theory_n3, '--', 'LineWidth', 1.2);

xlabel('Matrix size n');
ylabel('Execution time (ms, log-scale)');
title('Experimental data vs theoretical complexity models (logarithmic representation)');

legend('My DCT-II', 'JTransform DCT-II', ...
       'O(n)', 'O(n^2)', 'O(n^2 log n)', 'O(n^3)', ...
       'Location', 'northwest');

grid on;

%% =========================================================
% 4) EXPERIMENTAL DATA + THEORETICAL MODELS - linear scaling with normalization
%% =========================================================
f4 = figure;

% scaling factor to align theoretical curves with experimental magnitude
scale = max(CSV.my_time) / max(theory_n2log);

plot(n, CSV.my_time, '-o', 'LineWidth', 1.5);
hold on
plot(n, CSV.lib_time, '-s', 'LineWidth', 1.5);

plot(n, theory_n * scale, '--', 'LineWidth', 1.2);
plot(n, theory_n2 * scale, '--', 'LineWidth', 1.2);
plot(n, theory_n2log * scale, '--', 'LineWidth', 1.2);
plot(n, theory_n3 * scale, '--', 'LineWidth', 1.2);

hold off

xlabel('Matrix size n');
ylabel('Execution time (ms, scaled theoretical models)');
title('Experimental vs theoretical complexity models (linear scale with normalization)');

legend('My DCT-II', 'JTransform DCT-II', ...
       'O(n)', 'O(n^2)', 'O(n^2 log n)', 'O(n^3)', ...
       'Location', 'northwest');

grid on;

ylim([0 max(CSV.my_time) * 1.2]);


%% === SAVE ALL FIGURES ===
cmp_dir = "results";

if ~exist(cmp_dir, 'dir')
    mkdir(cmp_dir);
end

saveas(f1, fullfile(cmp_dir, 'figure_1_semilogy_data.png'));
saveas(f2, fullfile(cmp_dir, 'figure_2_linear_data.png'));
saveas(f3, fullfile(cmp_dir, 'figure_3_semilogy_data_vs_theory.png'));
saveas(f4, fullfile(cmp_dir, 'figure_4_linear_data_vs_theory_extended.png'));