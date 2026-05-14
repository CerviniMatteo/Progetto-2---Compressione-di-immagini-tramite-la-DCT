clear; clc; close all;

%% === LOAD DATA ===
datasets(1).name = "no_warmup";
datasets(1).table = readtable("times_vs_size.csv", ...
    'VariableNamingRule', 'preserve');

datasets(2).name = "with_warmup";
datasets(2).table = readtable("times_vs_size_with_JIT_warm_up.csv", ...
    'VariableNamingRule', 'preserve');

%% === OUTPUT DIRECTORY ===
cmp_dir = "results";

if ~exist(cmp_dir, 'dir')
    mkdir(cmp_dir);
end

%% =========================================================
% LOOP OVER DATASETS
%% =========================================================
for k = 1:length(datasets)

    %% === CURRENT DATASET ===
    CSV = datasets(k).table;
    dataset_name = datasets(k).name;

    %% === RENAME COLUMNS ===
    CSV.Properties.VariableNames = ...
        matlab.lang.makeValidName(CSV.Properties.VariableNames);

    CSV.Properties.VariableNames = ...
        strrep(CSV.Properties.VariableNames, ...
        'MyDCTTime_ms_', 'my_time');

    CSV.Properties.VariableNames = ...
        strrep(CSV.Properties.VariableNames, ...
        'LibDCTTime_ms_', 'lib_time');

    CSV.Properties.VariableNames = ...
        strrep(CSV.Properties.VariableNames, ...
        'Ratio_Lib_My_', 'ratio');

    CSV.Properties.VariableNames = ...
        strrep(CSV.Properties.VariableNames, ...
        'Size', 'n');

    %% === DATA ===
    n = CSV.n;

    %% === THEORETICAL COMPLEXITIES ===
    theory_n     = n;
    theory_n2    = n.^2;
    theory_n2log = n.^2 .* log2(n);
    theory_n3    = n.^3;

    %% =====================================================
    % 1) SEMILOG PLOT
    %% =====================================================
    f1 = figure;

    semilogy(n, CSV.my_time, '-o', 'LineWidth', 1.5);
    hold on
    semilogy(n, CSV.lib_time, '-s', 'LineWidth', 1.5);
    hold off

    xlabel('Matrix size n');
    ylabel('Execution time (ms, log-scale)');

    title(['Experimental execution time - ', dataset_name]);

    legend('My DCT-II', 'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;

    %% =====================================================
    % 2) LINEAR PLOT
    %% =====================================================
    f2 = figure;

    plot(n, CSV.my_time, '-o', 'LineWidth', 1.5);
    hold on
    plot(n, CSV.lib_time, '-s', 'LineWidth', 1.5);
    hold off

    xlabel('Matrix size n');
    ylabel('Execution time (ms)');

    title(['Linear execution time - ', dataset_name]);

    legend('My DCT-II', 'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;

    ylim([0 max(CSV.my_time) * 1.1]);

    %% =====================================================
    % 3) SEMILOG + THEORY
    %% =====================================================
    f3 = figure;

    semilogy(n, CSV.my_time, '-o', 'LineWidth', 1.5);
    hold on

    semilogy(n, CSV.lib_time, '-s', 'LineWidth', 1.5);

    semilogy(n, theory_n, '--', 'LineWidth', 1.2);
    semilogy(n, theory_n2, '--', 'LineWidth', 1.2);
    semilogy(n, theory_n2log, '--', 'LineWidth', 1.2);
    semilogy(n, theory_n3, '--', 'LineWidth', 1.2);

    hold off

    xlabel('Matrix size n');
    ylabel('Execution time (ms, log-scale)');

    title(['Experimental vs theoretical - ', dataset_name]);

    legend('My DCT-II', 'JTransform DCT-II', ...
           'O(n)', 'O(n^2)', ...
           'O(n^2 log n)', 'O(n^3)', ...
           'Location', 'northwest');

    grid on;

    %% =====================================================
    % 4) LINEAR + THEORY NORMALIZED
    %% =====================================================
    f4 = figure;

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
    ylabel('Execution time (ms)');

    title(['Linear normalized comparison - ', dataset_name]);

    legend('My DCT-II', 'JTransform DCT-II', ...
           'O(n)', 'O(n^2)', ...
           'O(n^2 log n)', 'O(n^3)', ...
           'Location', 'northwest');

    grid on;

    ylim([0 max(CSV.my_time) * 1.2]);

    %% =====================================================
    % SAVE FIGURES
    %% =====================================================
    saveas(f1, fullfile(cmp_dir, ...
        "figure_1_semilogy_" + dataset_name + ".png"));

    saveas(f2, fullfile(cmp_dir, ...
        "figure_2_linear_" + dataset_name + ".png"));

    saveas(f3, fullfile(cmp_dir, ...
        "figure_3_theory_semilogy_" + dataset_name + ".png"));

    saveas(f4, fullfile(cmp_dir, ...
        "figure_4_theory_linear_" + dataset_name + ".png"));

end